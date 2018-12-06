import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class Server {

    //Variables de classe
    private ArrayList<ServerThread> allThreads = new ArrayList<>();
    public ArrayList<Question> questionList = new ArrayList<>();
    private Hashtable<String, Integer> scoreBoard = new Hashtable<>();
    private Hashtable<String, Integer> tempScoreBoard = new Hashtable<>();
    private Hashtable<String, Integer> timeBoard = new Hashtable<>();
    private Hashtable<String, Integer> tempTimeBoard = new Hashtable<>();
    private int players;

    /**
     * Constructeur de classe par défaut
     */
    public Server() {
        setQuestionList(3);
        this.players = 2;
        while (true) {
            this.startListening(50000, 2);
        }
    }

    public void setQuestionList(int size) {
        int[] randomInt = new int[size];
        int i = 0;
        while (i < size) {
            boolean alreadyIn = false;
            int randomNumber = new Random().nextInt(6);

            for (int number : randomInt) {
                if (number == randomNumber)
                    alreadyIn = true;
            }

            if (!alreadyIn) {
                randomInt[i] = randomNumber;
                i++;
                questionList.add(new Question(randomNumber));
            }
        }
        System.out.println(randomInt);
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
        try {
            if (this.allThreads.size() == this.players) {
                for (ServerThread thread : this.allThreads) {
                    if (!thread.getStatus().equals("READY")) {
                        System.out.println("NOT READY !");
                        wait();
                        break;
                    }
                }
                System.out.println("Server > Tout les joueurs sont connectés, le jeu commence");
                notifyAll();
            } else {
                System.out.println("Server > En attente d'" + (this.players - this.allThreads.size()) + " joueur(s) supplémentaire(s) !");
                wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Renvoie le tableau des scores aux différents Threads. Mise en attente si ce dernier n'est pas complet.
     * @param name
     * @param score
     * @return
     */
    synchronized public List<Hashtable> setScoreBoard(String name, int score, int globalScore) {
        this.scoreBoard.put(name, globalScore);
        this.tempScoreBoard.put(name, score);

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

        List<Hashtable> scores = new ArrayList<>();
        scores.add(this.tempScoreBoard);
        scores.add(this.scoreBoard);

        return scores;
    }

    synchronized public List<Hashtable> setTime(String name, int time, int globalTime) {
        this.timeBoard.put(name, time);
        this.tempTimeBoard.put(name, globalTime);

        if (this.timeBoard.size() != this.allThreads.size()) {
            try {
                System.out.println("Server > En attente de temps supplémentaires (" + (this.allThreads.size()-this.timeBoard.size()) + " joueur(s)");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Server > Tout les temps ont été récupérés !");
            notifyAll();
        }

        List<Hashtable> times = new ArrayList<>();
        times.add(this.tempScoreBoard);
        times.add(this.scoreBoard);

        return times;
    }



    public static void main(String[] args) {
        Server s = new Server();
    }
}