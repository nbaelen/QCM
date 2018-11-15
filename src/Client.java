import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    //Variables de classe
    private Socket clientSocket;
    private BufferedReader clientBufferedReader;
    private PrintStream serverPrintStream;
    private Scanner scanner = new Scanner(System.in);

    public Client(String host, int port) {
        this.connectToServer(host, port);
        this.waitServerMessage();
    }

    public void connectToServer(String host, int port) {
        try {
            this.clientSocket = new Socket(host, port);
            this.clientBufferedReader= new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), "utf-8"));
            this.serverPrintStream = new PrintStream(this.clientSocket.getOutputStream(), true, "utf-8");
        } catch (IOException e) {
            System.err.println("Connexion au serveur impossible !");
        }
    }

    public void waitServerMessage() {
        String serverMessage = "";
        while (true) {
            try {
                serverMessage = clientBufferedReader.readLine();

                if (serverMessage.equals("@")) {
                    this.answerServerQuestion();
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
        String clientAnswer = this.scanner.nextLine();
        System.out.println("Client > " + clientAnswer);
        serverPrintStream.println(clientAnswer);
        this.waitServerMessage();
    }

    public static void main(String[] args) {
        Client c = new Client("localhost", 50000);
        c.waitServerMessage();
    }
}
