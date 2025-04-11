import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) throws IOException {
        System.out.println("💬 Chat Server started...");
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {
            // Accept a new client
            Socket clientSocket = serverSocket.accept();
            System.out.println("✅ New client connected: " + clientSocket);
            
            // Handle in a new thread
            new ClientHandler(clientSocket).start();
        }
    }

    // Inner class to handle client communication
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Setup input/output
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Add this client's writer to the set
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Read and broadcast messages
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("📨 Message received: " + message);
                    broadcast(message);
                }

            } catch (IOException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } finally {
                // Remove this client on disconnect
                try {
                    socket.close();
                } catch (IOException e) {}
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
