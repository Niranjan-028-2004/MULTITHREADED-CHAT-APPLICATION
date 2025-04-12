import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Server is running...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected!");
                ClientHandler handler = new ClientHandler(socket);
                clientHandlers.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all clients
    public static void broadcast(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler ch : clientHandlers) {
                if (ch != sender) {
                    ch.sendMessage(message);
                }
            }
        }
    }

    // Remove a client
    public static void removeClient(ClientHandler handler) {
        clientHandlers.remove(handler);
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String name = in.readLine();
            System.out.println(name + " has joined the chat.");

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(name + ": " + msg);
                ChatServer.broadcast(name + ": " + msg, this);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
