import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class Server {

    //Variables de classe
    private ArrayList<ServerThread> allThreads = new ArrayList<>();
    private Hashtable<String, Integer> scoreBoard = new Hashtable<>();
    public boolean canStart = false;
    private int players = 2;

    /**
     * Constructeur de classe par défaut
     */
    public Server() {
        while (true) {
            this.startListening(50000, 2);
            this.canStart = true;
        }
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

    /**
     * Renvoie le tableau des scores aux différents Threads. Mise en attente si ce dernier n'est pas complet.
     * @param name
     * @param score
     * @return
     */
    synchronized public Hashtable<String, Integer> setScoreBoard(String name, int score) {
        this.scoreBoard.put(name, score);

        if (this.scoreBoard.size() != this.allThreads.size()) {
            try {
                System.out.println("Waiting for other scores");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Notify every Threads, scores are OK");
            notifyAll();
        }

        return this.scoreBoard;
    }

    /**
     * Met en attente les Thread pour les faire commencer ensemble
     */
    synchronized public void canStart() {
        System.out.println(this);
        System.out.println("Entering canStart()");
        if (this.allThreads.size() == this.players)  {
            System.out.println("Notifying all Threads, Game can Start");
            notifyAll();
        } else {
            System.out.println("Waiting for other players to connect");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
    }
}