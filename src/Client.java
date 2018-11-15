import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("localhost", 50000)) {
            //Notifie la connection
            System.out.println("Connexion établie entre le client ("
                    + clientSocket.getLocalSocketAddress() + ") et le serveur ("
                    + clientSocket.getRemoteSocketAddress() + ")");

            //Création des objets
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "utf-8"));
            PrintStream ps = new PrintStream(clientSocket.getOutputStream(), true, "utf-8");
            Scanner sc = new Scanner(System.in);
            Boolean repeat = true;

            while (repeat == true) {
                //Lecture de la demande du serveur
                System.out.println("Server > " + br.readLine());

                //Lecture de la réponse du client et envoie au serveur
                String lookFor  = sc.nextLine();
                System.out.println("Client > Vous allez rechercher le mot clé : " + lookFor);
                ps.println(lookFor);

                //Lecture de la réponse du serveur
                System.out.println(br.readLine());

                //Réponse au serveur
                String wantToRepeat = sc.nextLine();
                System.out.println(wantToRepeat);
                if (!(wantToRepeat.equals("y"))) {
                    repeat = false;
                }
                ps.println(wantToRepeat);
            }
            clientSocket.close();
            System.out.println("Deconnexion du serveur");
        } catch (IOException e) {
            System.err.println("Connexion au serveur impossible");
        }
    }
}
