import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Question {

    private BufferedReader questionFileReader;

    private String question;
    private String[] possibleAnswers;
    private String answer;
    private int score;

    /**
     * Constructeur de classe. Permet la génération d'une question aléatoire contenue dans le fichier questions.txt
     */
    public Question(int number) {
        try {
            this.questionFileReader = new BufferedReader(new FileReader("questions.txt"));
            this.setInformations(this.getInformations(number));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère la question renseignée à la @param lineNumber fourni
     * @param lineNumber
     * @return
     * @throws IOException
     */
    public String getInformations(int lineNumber) throws IOException {
        String line = "";
        int counter = 0;
        while ((line = questionFileReader.readLine()) != null) {
            if (counter == lineNumber) {
                return line;
            }
            counter++;
        }
        return "";
    }

    /**
     * Assigne les variables de classe à l'aide d'une chaine d'informations fournie
     * @param informations
     */
    public void setInformations(String informations) {
        String[] informationsArray = informations.split(";",-1);
        this.question = informationsArray[0];
        this.possibleAnswers = informationsArray[1].split(",", -1);
        this.answer = informationsArray[2];
        this.score = Integer.parseInt(informationsArray[3]);
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getPossibleAnswers() {
        return possibleAnswers;
    }

    public int getScore() {
        return score;
    }
}
