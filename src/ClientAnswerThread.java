import java.util.Scanner;

public class ClientAnswerThread extends Thread {
    private String clientAnswer;
    private long time;

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        time = System.currentTimeMillis();
        this.clientAnswer = scanner.nextLine();
        time = System.currentTimeMillis() - time;
    }

    public String getClientAnswer() {
        return clientAnswer;
    }

    public long getTime() {
        return time;
    }
}
