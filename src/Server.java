import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    //Variables de classe
    public ArrayList<ServerThread> allThreads = new ArrayList<>();

    public Server() {
        this.startListening(50000, 2);
    }

    public void startListening(int port, int players) {
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            System.out.println(".... Serveur à l'écoute ....");
            while (allThreads.size() < players) {
                Socket serviceSocket = listenSocket.accept();
                allThreads.add(new ServerThread(String.valueOf(allThreads.size()), serviceSocket));
            }
        } catch (IOException ex) {
            System.out.println("Connexion impossible.");

        }
    }

    public void threadQuestion() {
        for (ServerThread thread : this.allThreads) {
            thread.question();
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
    }
}