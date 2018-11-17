import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Client {

    //Variables de classe
    private Socket clientSocket;
    private BufferedReader clientBufferedReader;
    private PrintStream serverPrintStream;
    private Scanner scanner = new Scanner(System.in);

    /**
     * Constructeur de classe par défaut
     */
    public Client() {
        this.connectToServer("localhost", 50000);
        this.waitServerMessage();
    }

    //Fonctions en charge de la gestion de la transmission avec le serveur

    /**
     * Connecte le client à un serveur indiqué par son @param host et son @param port
     * @param host
     * @param port
     */
    public void connectToServer(String host, int port) {
        try {
            this.clientSocket = new Socket(host, port);
            this.clientBufferedReader= new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), "utf-8"));
            this.serverPrintStream = new PrintStream(this.clientSocket.getOutputStream(), true, "utf-8");
        } catch (IOException e) {
            System.err.println("Connexion au serveur impossible !");
        }
    }

    /**
     * Déconnecte le client du serveur actuel
     */
    public void disconnectFromServer() {
        try {
            System.out.println("Déconnexion du serveur " + this.clientSocket.getRemoteSocketAddress());
            this.clientBufferedReader.close();
            this.serverPrintStream.close();
            this.clientSocket.close();
        } catch (IOException e) {
            System.out.println("Déconnexion du serveur impossible !");
        }
    }

    //Fonctions en charge des échanges de messages avec le serveur

    /**
     * Met le client en attente afin que le serveur puisse envoyer des informations
     */
    public void waitServerMessage() {
        String serverMessage = "";
        while (true) {
            try {
                serverMessage = clientBufferedReader.readLine();

                if (serverMessage.equals("???")) {
                    this.answerServerQuestion();
                    break;
                } else if (serverMessage.equals("###")) {
                    this.disconnectFromServer();
                    break;
                } else {
                    System.out.println("Server > " + serverMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet au client de répondre aux questions posées par le serveur suite à une transmission contenant "???"
     */
    public void answerServerQuestion() {
        String clientAnswer = this.scanner.nextLine();
        serverPrintStream.println(clientAnswer);
        this.waitServerMessage();
    }

    public static void main(String[] args) {
        Client c = new Client();
    }
}
