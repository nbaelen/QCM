import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class Question {

    private BufferedReader questionFileReader;

    private String question;
    private String[] possibleAnswers;
    private String answer;

    public Question(int index) {
        try {
            this.questionFileReader = new BufferedReader(new FileReader("questions.txt"));
            this.setInformations(this.getInformations(index));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInformations(int lineNumber) throws IOException {
        String line;
        int counter = 0;

        while ((line = questionFileReader.readLine()) != null) {
            counter++;
            System.out.println(line);
            if (counter == lineNumber) {
                return line;
            }
        }
        return "";
    }

    public void setInformations(String informations) {
        System.out.println("Infos = " + informations);
        String[] informationsArray = informations.split(";",-1);

        this.question = informationsArray[0];
        this.possibleAnswers = informationsArray[1].split(",", -1);
        this.answer = informationsArray[2];
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

    @Override
    public String toString() {
        return this.question + this.possibleAnswers + this.answer;
    }
}
