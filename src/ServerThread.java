import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

class ServerThread implements Runnable {

    private String name;
    private Socket serviceSocket;      // Socket de service du client
    private PrintStream clientPrintStream; // PrintStream du client
    private BufferedReader serverBufferedReader; // BufferedReader du client
    private String clientPseudo;

    public ServerThread(String name, Socket serviceSocket) {
        this.name = name + 1;
        this.serviceSocket = serviceSocket;

        //Création des flux pour discuter avec le client
        try {
            this.clientPrintStream = new PrintStream(serviceSocket.getOutputStream(), true, "utf-8");
            this.serverBufferedReader = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream(), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.run();
    }

    @Override
    public void run() {
        this.notifyConnexion();
        this.sendToClient("Bienvenue au QCM. Vous êtes connecté au serveur");
        this.askClientPseudo();
    }

    public void notifyConnexion() {
        System.out.println("Thread " + this.name + " > Connexion établie avec un client (" + serviceSocket.getRemoteSocketAddress() + ")");
    }

    public void sendToClient(String message) {
        System.out.println("Thread " + this.name + " > " + message);
        this.clientPrintStream.println(message);
    }

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

    public void askClientPseudo() {
        this.sendToClient("Quel est votre pseudo ?");
        this.clientPseudo = this.waitClientAnswer();
    }
}