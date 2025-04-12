import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.print("Enter your name: ");
            String name = userInput.readLine();
            out.println(name);

            // Thread to read messages from server
            Thread readThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });

            readThread.start();

            // Main thread handles sending messages
            String msg;
            while ((msg = userInput.readLine()) != null) {
                out.println(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
