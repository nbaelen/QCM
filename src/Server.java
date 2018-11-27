import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Server {

    //Variables de classe
    private ArrayList<ServerThread> allThreads = new ArrayList<>();
    public ArrayList<Question> questionList = new ArrayList<>();
    private Hashtable<String, Integer> scoreBoard = new Hashtable<>();
    private int players = 2;

    /**
     * Constructeur de classe par défaut
     */
    public Server() {
        setQuestionList(3);
        while (true) {
            this.startListening(50000, 2);
        }
    }

    public void setQuestionList(int number) {
        for (int i=0; i<number; i++)
            questionList.add(new Question());
    }

    public ArrayList<Question> getQuestionList() {
        return questionList;
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
     * Met en attente les Thread pour les faire commencer ensemble
     */
    synchronized public void canStart() {
        if (this.allThreads.size() == this.players)  {
            System.out.println("Server > Tout les joueurs sont connectés, le jeu commence");
            notifyAll();
        } else {
            System.out.println("Server > En attente d'" + (this.players-this.allThreads.size()) + " joueur(s) supplémentaire(s) !");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
                System.out.println("Server > En attente de résultats supplémentaires (" + (this.allThreads.size()-this.scoreBoard.size()) + " joueur(s)");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Server > Tout les résultats ont été récupérés !");
            notifyAll();
        }

        return this.scoreBoard;
    }

    public static void main(String[] args) {
        Server s = new Server();
    }
}