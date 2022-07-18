package chat;

import java.io.IOException;
import java.util.Scanner;

public class CheckServerMessage extends UDPServer implements Runnable {
    private byte[] fromServer = new byte[1024];
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            if (scanner.hasNextLine()) {
                try {
                    fromServer = ("SERVER: " + scanner.nextLine()).getBytes();
                    sendAll(fromServer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
