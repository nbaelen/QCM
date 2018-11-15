import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    //Variables de classe
    private ArrayList<ServerThread> allThreads = new ArrayList<>();
    public boolean canStart = false;

    /**
     * Constructeur de classe par défaut
     */
    public Server() {
        this.startListening(50000, 1);
        System.out.println("canStart");
        this.canStart = true;
    }

    /**
     * Fonction en charge d'écouter le @param port jusqu'à ce que le nombre @param players soit connectés
     * @param port
     * @param players
     */
    public void startListening(int port, int players) {

        while (this.allThreads.size() < players) {
            try (ServerSocket listenSocket = new ServerSocket(port)) {
                System.out.println(".... Serveur à l'écoute ....");
                Socket serviceSocket = listenSocket.accept();
                this.allThreads.add(new ServerThread(String.valueOf(this.allThreads.size()+1), this, serviceSocket));
                this.allThreads.get(allThreads.size() - 1).start();
            } catch (IOException ex) {
                System.out.println("Connexion impossible.");
            }
        }
    }

    public void threadQuestion() {
        for (ServerThread thread : this.allThreads) {
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
    }
}