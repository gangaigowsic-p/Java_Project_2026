package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private String username;
    
    public ChatClient(String username, Scanner scanner) {
        this.username = username;
        this.scanner = scanner;
    }

    public void start() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send login
            out.println("LOGIN|" + username);
            
            String response = in.readLine();
            if (response != null && response.equals("SERVER|LOGIN_SUCCESS")) {
                System.out.println("Connected to server successfully!");
                
                // Start reader thread
                Thread readerThread = new Thread(new ServerReader());
                readerThread.start();
                
                // Main thread handles input
                handleUserInput();
                
            } else {
                System.out.println("Login failed or already logged in on server.");
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    private void handleUserInput() {
        printChatMenu();
        
        while (true) {
            String input = scanner.nextLine();
            
            if (input.equals("8") || input.equalsIgnoreCase("/quit")) {
                out.println("CMD|/quit");
                break;
            } else if (input.equals("1")) {
                out.println("CMD|/leave"); // Ensure we leave any room
                System.out.println("Joined public chat. Type your messages:");
            } else if (input.equals("2")) {
                System.out.print("Enter recipient username: ");
                String recipient = scanner.nextLine();
                System.out.print("Enter message: ");
                String msg = scanner.nextLine();
                out.println("MSG|/msg " + recipient + " " + msg);
            } else if (input.equals("3")) {
                System.out.print("Enter new room name: ");
                String roomName = scanner.nextLine();
                out.println("CMD|/create " + roomName);
            } else if (input.equals("4")) {
                System.out.print("Enter room name to join: ");
                String roomName = scanner.nextLine();
                out.println("CMD|/join " + roomName);
                System.out.println("Type your messages (they will go to the room):");
            } else if (input.equals("5")) {
                out.println("CMD|/list");
            } else if (input.equals("6")) {
                out.println("CMD|/users");
            } else if (input.equals("7")) {
                System.out.println("Fetching recent public messages... (Check database)");
                // In a complete app, might request from server, or we can just keep chat flowing
            } else if (input.startsWith("/")) {
                out.println("CMD|" + input);
            } else if (!input.trim().isEmpty()) {
                // Regular message
                out.println("MSG|" + input);
            }
        }
        
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printChatMenu() {
        System.out.println("\n========================================");
        System.out.println("             CHAT MENU");
        System.out.println("========================================");
        System.out.println("1. Join public chat");
        System.out.println("2. Send private message");
        System.out.println("3. Create chat room");
        System.out.println("4. Join chat room");
        System.out.println("5. List chat rooms");
        System.out.println("6. View online users");
        System.out.println("7. View recent messages");
        System.out.println("8. Logout / Quit");
        System.out.println("You can also type messages directly. Use /quit to exit.");
        System.out.println("========================================\n");
    }

    private class ServerReader implements Runnable {
        @Override
        public void run() {
            try {
                String serverMsg;
                while ((serverMsg = in.readLine()) != null) {
                    if (serverMsg.startsWith("SERVER|")) {
                        System.out.println("[SERVER]: " + serverMsg.substring(7));
                    } else if (serverMsg.startsWith("PUBLIC|")) {
                        System.out.println("[PUBLIC] " + serverMsg.substring(7));
                    } else if (serverMsg.startsWith("PRIVATE|")) {
                        System.out.println("[PRIVATE] " + serverMsg.substring(8));
                    } else if (serverMsg.startsWith("ROOM|")) {
                        System.out.println("[ROOM] " + serverMsg.substring(5));
                    } else {
                        System.out.println(serverMsg);
                    }
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }
}
