import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    //Variables de classe
    private Socket clientSocket;
    private BufferedReader clientBufferedReader;
    private PrintStream serverPrintStream;


    //Constructeur par défaut
    public Client() {
        this.connectToServer("localhost",50000);
    }

    public void connectToServer(String host, int port) {
        try {
            this.clientSocket = new Socket(host, port);
            System.out.println("Client > Connexion établie avec le serveur (" + this.clientSocket.getRemoteSocketAddress() + ")");
            this.clientBufferedReader= new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), "utf-8"));
            this.serverPrintStream = new PrintStream(this.clientSocket.getOutputStream(), true, "utf-8");
            System.out.println("Server > " + this.clientBufferedReader.readLine());
        } catch (IOException e) {
            System.err.println("Connexion au serveur impossible !");
        }
    }

    public void waitServer() {
        try {
            System.out.println("Server > " + clientBufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.waitServer();
    }
}
