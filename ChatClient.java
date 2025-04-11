import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_IP = "localhost"; // Change if server is remote
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("🔌 Connected to chat server");

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        // Thread to read messages from the server
        Thread readThread = new Thread(() -> {
            String messageFromServer;
            try {
                while ((messageFromServer = input.readLine()) != null) {
                    System.out.println("👥 " + messageFromServer);
                }
            } catch (IOException e) {
                System.out.println("❌ Connection closed.");
            }
        });
        readThread.start();

        // Main thread: read user input and send to server
        System.out.print("Enter your name: ");
        String name = userInput.readLine();
        output.println(name + " has joined the chat.");

        String userMessage;
        while ((userMessage = userInput.readLine()) != null) {
            output.println(name + ": " + userMessage);
        }

        socket.close();
    }
}
