import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

class ServerThread extends Thread {

    //Variables utilisées pour le fonctionnement du Thread
    private String name;
    private Server server;
    private Socket serviceSocket; // Socket de service du client
    private PrintStream clientPrintStream; // PrintStream du client
    private BufferedReader serverBufferedReader; // BufferedReader du client

    //Variables utilisées pour le fonctionnement du jeu
    private String clientPseudo;
    private int score;

    /**
     * Constructeur de classe
     *
     * @param name
     * @param serviceSocket
     */
    public ServerThread(String name, Server server, Socket serviceSocket) {
        this.name = name;
        this.server = server;
        this.serviceSocket = serviceSocket;

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
        this.startGame(3);
        this.endTransmission();

        //A changer c'est pas beau
        /* while (true) {
            if (this.server.canStart == true) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("En attente d'autres joueurs ...");
        } */


    }

    //Fonctions en charge des échanges de messages avec le client

    /**
     * Permet d'envoyer un @param message au client, et de le notifier dans la console
     *
     * @param message
     */
    public void sendToClient(String message) {
        if (!message.equals("???") || !message.equals("###"))
            System.out.println("Thread " + this.name + " > " + message);
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
     *
     * @return clientAnswer
     */
    public String waitClientAnswer() {
        this.sendToClient("???");
        String clientAnswer = "";
        try {
            clientAnswer = this.serverBufferedReader.readLine();
            System.out.println("Client " + this.name + " > " + clientAnswer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientAnswer;
    }

    /**
     * Notifie la connexion entre le thread et le client
     */
    public void notifyConnexion() {
        System.out.println("Thread " + this.name + " > Connexion établie avec un client (" + serviceSocket.getRemoteSocketAddress() + ")");
        this.sendToClient("Bienvenue au QCM. Vous êtes connecté au serveur");
    }

    //Fonctions relatives au jeu

    /**
     * Demande au client son pseudo et le stocke dans la variable de classe associée
     */
    public void startGame(int numberQuestion) {
        for (int i = 0; i < numberQuestion; i++) {
            if (askQuestion())
                this.addScore(1);
        }
        this.sendToClient("Votre score : " + this.server.setScoreBoard(this.name, this.score).get(this.name));

    }

    /**
     * Demande au client son pseudonyme et l'assigne
     */
    public void askClientPseudo() {
        this.sendToClient("Quel est votre pseudo ?");
        this.clientPseudo = this.waitClientAnswer();
        this.sendToClient("Bonjour " + this.clientPseudo + " !");
    }

    /**
     * Pose une question au joueur, et valide ou non sa réponse à l'aide d'un boolean
     *
     * @return goodAnswer
     */
    public boolean askQuestion() {
        Question q = new Question();

        this.sendToClient(q.getQuestion());
        for (String possibleAnswer : q.getPossibleAnswers()) {
            this.sendToClient(possibleAnswer);
        }
        this.sendToClient("Votre réponse ?");

        if (waitClientAnswer().toLowerCase().equals(q.getAnswer().toLowerCase())) {
            this.sendToClient("Bonne réponse ! Bravo !");
            return true;
        } else {
            this.sendToClient("Mauvaise réponse... La bonne réponse était : " + q.getAnswer());
            return false;
        }
    }

    //Fonctions relatives à la gestion des scores
    public void addScore(int point) {
        this.score += point;
    }

    public String getScore() {
        return "Votre score est de " + this.score + " points !";
    }
}