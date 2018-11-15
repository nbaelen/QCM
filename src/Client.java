import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    //Variables de classe
    private Socket clientSocket;
    private BufferedReader clientBufferedReader;
    private PrintStream serverPrintStream;
    private Scanner scanner;

    public Client(String host, int port) {
        this.connectToServer(host, port);
        this.scanner = new Scanner(System.in);
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

    public void waitServerMessage() {
        String serverMessage = "";
        while (true) {
            try {
                serverMessage = clientBufferedReader.readLine();

                if (serverMessage.contains("?")) {
                    System.out.println("Server > " + serverMessage);
                    this.answerServerQuestion();
                    break;
                } else if (serverMessage.equals("@")) {
                    break;
                } else {
                    System.out.println("Server > " + serverMessage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void answerServerQuestion() {
        String clientanswer = scanner.nextLine();
        System.out.println("Client > " + clientanswer);
        serverPrintStream.println(clientanswer);
        this.waitServerMessage();
    }

    public static void main(String[] args) {
        Client c = new Client("localhost", 50000);
        c.waitServerMessage();
    }
}
