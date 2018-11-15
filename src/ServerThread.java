import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

class ServerThread extends Thread {

    private Socket ss;      // Socket de service du client
    private PrintStream ps; // PrintStream du client
    private BufferedReader br; // BufferedReader du client

    public ServerThread(Socket ss) {
        this.ss = ss;
        try {
            this.ps = new PrintStream(ss.getOutputStream(), true, "utf-8");
            this.br = new BufferedReader(new InputStreamReader(ss.getInputStream(), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

    }
}