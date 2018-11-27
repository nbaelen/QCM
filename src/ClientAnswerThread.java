import java.util.Scanner;

public class ClientAnswerThread extends Thread {
    private String clientAnswer;

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        this.clientAnswer = scanner.nextLine();
    }

    public String getClientAnswer() {
        return clientAnswer;
    }
}
