package app;

import dao.UserDAO;
import util.DatabaseSetup;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int PORT = 5000;
    // Map of connected clients: username -> ClientHandler
    public static Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       REAL TIME CHAT SERVER");
        System.out.println("========================================");
        System.out.println("Connecting to MySQL...");
        
        try {
            // Attempt to initialize database
            DatabaseSetup.initialize();
            System.out.println("Database initialization finished.");
            
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Chat server started on port " + PORT + ".");
                System.out.println("Waiting for clients...");
                
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New connection: " + clientSocket.getInetAddress());
                    
                    ClientHandler handler = new ClientHandler(clientSocket);
                    new Thread(handler).start();
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Critical failure starting server: " + e.getMessage());
        }
    }
}
