import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

class ServerThread extends Thread {

    //Variables utilisées pour le fonctionnement du Thread
    private Server server;
    private Socket serviceSocket; // Socket de service du client
    private PrintStream clientPrintStream; // PrintStream du client
    private BufferedReader serverBufferedReader; // BufferedReader du client
    private String status = "IDLE";

    //Variables utilisées pour le fonctionnement du jeu
    private String clientPseudo;
    private int score;

    /**
     * Constructeur de classe
     * @param name
     * @param serviceSocket
     */
    public ServerThread(String name, Server server, Socket serviceSocket) {
        this.server = server;
        this.serviceSocket = serviceSocket;
        this.setName(name);

        //Création des flux pour discuter avec le client
        try {
            this.clientPrintStream = new PrintStream(serviceSocket.getOutputStream(), true, "utf-8");
            this.serverBufferedReader = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream(), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.notifyConnexion();
        this.askClientPseudo();
        this.sendToClient("Le jeu va commencer, veuillez patienter");
        this.server.canStart();
        this.playGame();
        this.endTransmission();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    //Fonctions en charge des échanges de messages avec le client

    /**
     * Permet d'envoyer un @param message au client, et de le notifier dans la console
     * @param message
     */
    public void sendToClient(String message) {
        if (!message.contains("???") && !message.contains("###"))
            System.out.println("Thread " + this.getName() + " > " + message);
        this.clientPrintStream.println(message);
    }

    /**
     * Termine la communication avec un client en envoyant un caractère spécifique
     */
    public void endTransmission() {
        this.sendToClient("###");
    }

    /**
     * Permet d'attendre la réponse d'une client, et la renvoie
     * @return clientAnswer
     */
    public String waitClientAnswer() {
        this.sendToClient("???");

        String clientAnswer = "";
        try {
            clientAnswer = this.serverBufferedReader.readLine();
            System.out.println("Client " + this.getName() + " > " + clientAnswer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientAnswer;
    }

    /**
     * Notifie la connexion entre le thread et le client
     */
    public void notifyConnexion() {
        System.out.println("Thread " + this.getName() + " > Connexion établie avec un client (" + serviceSocket.getRemoteSocketAddress() + ")");
        this.sendToClient("Bienvenue au QCM. Vous êtes connecté au serveur");
    }

    //Fonctions relatives au jeu

    /**
     * Demande au client son pseudo et le stocke dans la variable de classe associée A ENLEVER C PO JOLI
     */
    public void playGame () {
        ArrayList<Question> questionList = this.server.getQuestionList();

        for (Question question: questionList) {
            if (askQuestion(question)) {
                this.addScore(question.getScore());

                List scores = this.server.setScoreBoard(this.clientPseudo, question.getScore(), this.score);

                this.sendToClient("------------------------------------------------------------------");
                Hashtable<String, Integer> tempScores = (Hashtable<String, Integer>) scores.get(0);
                Set<String> tempKeys = tempScores.keySet();
                for (String key : tempKeys) {
                    sendToClient(key + " a marqué " + tempScores.get(key) + " point(s) !");
                }
                this.sendToClient("------------------------------------------------------------------");
                Hashtable<String, Integer> globalScores = (Hashtable<String, Integer>) scores.get(1);
                Set<String> globalKeys = globalScores.keySet();
                for (String key : globalKeys) {
                    sendToClient(key + " a marqué un total de " + globalScores.get(key) + " point(s) !");
                }
                this.sendToClient("------------------------------------------------------------------");
            }
        }
    }

    /**
     * Demande au client son pseudonyme et l'assigne
     */
    public void askClientPseudo() {
        this.sendToClient("Quel est votre pseudo ?");
        this.clientPseudo = this.waitClientAnswer();
        this.sendToClient("Bonjour " + this.clientPseudo + " !");
        this.setStatus("READY");
    }

    /**
     * Pose une question au joueur, et valide ou non sa réponse à l'aide d'un boolean
     * @return goodAnswer
     */
    public boolean askQuestion(Question question) {
        this.sendToClient(question.getQuestion());
        for (String possibleAnswer : question.getPossibleAnswers()) {
            this.sendToClient(possibleAnswer);
        }
        this.sendToClient("Votre réponse ?");

        String answer = waitClientAnswer();
        if (answer.toLowerCase().equals(question.getAnswer().toLowerCase())) {
            this.sendToClient("Bonne réponse ! Bravo !");
            return true;
        } else if (answer.equals("///")) {
            this.sendToClient("Vous n'avez pas fourni de réponse, merci de répondre dans le temps imparti !");
            return false;
        } else {
            this.sendToClient("Mauvaise réponse... La bonne réponse était : " + question.getAnswer());
            return false;
        }
    }

    //Fonctions relatives à la gestion des scores
    public void addScore(int point) {
        this.score += point;
    }
}