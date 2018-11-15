import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

class ServerThread extends Thread {

    private String name;
    private Server server;
    private Socket serviceSocket; // Socket de service du client
    private PrintStream clientPrintStream; // PrintStream du client
    private BufferedReader serverBufferedReader; // BufferedReader du client
    private String clientPseudo;

    /**
     * Constructeur de classe
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

        if (askQuestion()) {
            this.sendToClient("Bravo !");
        }

    }

    /**
     * Notifie la connexion entre le thread et le client
     */
    public void notifyConnexion() {
        System.out.println("Thread " + this.name + " > Connexion établie avec un client (" + serviceSocket.getRemoteSocketAddress() + ")");
        this.sendToClient("Bienvenue au QCM. Vous êtes connecté au serveur");
    }

    /**
     * Permet d'envoyer un @param message au client, et de le notifier dans la console
     * @param message
     */
    public void sendToClient(String message) {
        if (!message.equals("@"))
            System.out.println("Thread " + this.name + " > " + message);
        this.clientPrintStream.println(message);
    }

    public void endMessage() {
        this.sendToClient("@");
    }

    /**
     * Permet d'attendre la réponse d'une client, et la renvoie
     * @return clientAnswer
     */
    public String waitClientAnswer() {
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
     * Demande au client son pseudo et le stocke dans la variable de classe associée
     */
    public void askClientPseudo() {
        this.sendToClient("Quel est votre pseudo ?");
        this.endMessage();
        this.clientPseudo = this.waitClientAnswer();
        this.sendToClient("Bonjour " + this.clientPseudo + " !");
    }

    public boolean askQuestion() {
        Question q = new Question(2);
        this.sendToClient(q.getQuestion());

        for (String possibleAnswer : q.getPossibleAnswers()) {
            this.sendToClient(possibleAnswer);
        }
        this.endMessage();

        String clientAnswer = waitClientAnswer();
        System.out.println(clientAnswer);
        System.out.println(q.getAnswer());
        if (clientAnswer.equals(q.getAnswer())) {
            System.out.println("true");
            return true;
        } else {
            System.out.println("false");
            return false;
        }
    }
}