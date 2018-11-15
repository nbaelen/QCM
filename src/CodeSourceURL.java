import java.io.*;
import java.net.*;

public class CodeSourceURL {

    private String URLname;

    //Constructeur de classe
    public CodeSourceURL (String pURLname) {
        this.URLname = pURLname;
    }

    public boolean checkContent(String word) {
        boolean wordInContent = false;
        try {
            URL u = new URL(this.URLname);
            URLConnection uc = u.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                if (inputLine.contains(word)) {
                   wordInContent = true;
                   break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur : " + e);
        }
        return wordInContent;
    }
}
