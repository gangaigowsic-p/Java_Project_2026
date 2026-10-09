package app;

import dao.ChatRoomDAO;
import dao.MessageDAO;
import dao.UserDAO;
import model.ChatRoom;
import model.Message;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private User currentUser;
    private ChatRoom currentRoom;

    private UserDAO userDAO = new UserDAO();
    private MessageDAO messageDAO = new MessageDAO();
    private ChatRoomDAO chatRoomDAO = new ChatRoomDAO();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("LOGIN|")) {
                    handleLogin(inputLine);
                } else if (inputLine.startsWith("CMD|")) {
                    handleCommand(inputLine.substring(4));
                } else if (inputLine.startsWith("MSG|")) {
                    handleMessage(inputLine.substring(4));
                }
            }
        } catch (IOException e) {
            // Client disconnected abruptly
        } finally {
            disconnect();
        }
    }

    private void handleLogin(String authString) {
        // Format: LOGIN|username
        String[] parts = authString.split("\\|");
        if (parts.length >= 2) {
            String username = parts[1];
            User user = userDAO.getUserByUsername(username);
            if (user != null) {
                if (ChatServer.onlineUsers.containsKey(username)) {
                    out.println("SERVER|ALREADY_LOGGED_IN");
                } else {
                    currentUser = user;
                    ChatServer.onlineUsers.put(username, this);
                    out.println("SERVER|LOGIN_SUCCESS");
                    broadcast("SERVER|User " + username + " has joined the server.", null);
                }
            } else {
                out.println("SERVER|LOGIN_FAILED");
            }
        }
    }

    private void handleCommand(String command) {
        if (currentUser == null) return;
        
        String[] parts = command.split(" ", 2);
        String cmd = parts[0];

        switch (cmd) {
            case "/join":
                if (parts.length == 2) {
                    joinRoom(parts[1]);
                }
                break;
            case "/leave":
                leaveRoom();
                break;
            case "/list":
                listRooms();
                break;
            case "/users":
                listUsers();
                break;
            case "/create":
                if (parts.length == 2) {
                    createRoom(parts[1]);
                }
                break;
            case "/quit":
                disconnect();
                break;
            default:
                out.println("SERVER|Unknown command.");
        }
    }

    private void handleMessage(String messageContent) {
        if (currentUser == null) return;

        if (messageContent.startsWith("/msg ")) {
            // Private message: /msg username message
            String[] parts = messageContent.split(" ", 3);
            if (parts.length == 3) {
                sendPrivateMessage(parts[1], parts[2]);
            } else {
                out.println("SERVER|Usage: /msg username message");
            }
        } else if (currentRoom != null) {
            // Send to room
            sendRoomMessage(messageContent);
        } else {
            // Public message
            sendPublicMessage(messageContent);
        }
    }

    private void sendPublicMessage(String content) {
        Message msg = new Message(0, currentUser.getUserId(), null, null, content, "PUBLIC", null);
        messageDAO.saveMessage(msg);
        broadcast("PUBLIC|[" + currentUser.getUsername() + "]: " + content, null);
    }

    private void sendPrivateMessage(String recipientName, String content) {
        User recipient = userDAO.getUserByUsername(recipientName);
        if (recipient == null) {
            out.println("SERVER|User does not exist.");
            return;
        }

        ClientHandler recipientHandler = ChatServer.onlineUsers.get(recipientName);
        if (recipientHandler == null) {
            out.println("SERVER|User is offline.");
            // Still save to DB
        } else {
            recipientHandler.sendMessage("PRIVATE|[" + currentUser.getUsername() + " -> " + recipientName + "]: " + content);
            out.println("PRIVATE|[" + currentUser.getUsername() + " -> " + recipientName + "]: " + content);
        }
        
        Message msg = new Message(0, currentUser.getUserId(), recipient.getUserId(), null, content, "PRIVATE", null);
        messageDAO.saveMessage(msg);
    }

    private void sendRoomMessage(String content) {
        Message msg = new Message(0, currentUser.getUserId(), null, currentRoom.getRoomId(), content, "ROOM", null);
        messageDAO.saveMessage(msg);
        
        String formatted = "ROOM|(" + currentRoom.getRoomName() + ") [" + currentUser.getUsername() + "]: " + content;
        for (ClientHandler handler : ChatServer.onlineUsers.values()) {
            if (handler.currentRoom != null && handler.currentRoom.getRoomId() == currentRoom.getRoomId()) {
                handler.sendMessage(formatted);
            }
        }
    }

    private void joinRoom(String roomName) {
        ChatRoom room = chatRoomDAO.getRoomByName(roomName);
        if (room != null) {
            currentRoom = room;
            out.println("SERVER|Joined room: " + roomName);
        } else {
            out.println("SERVER|Room does not exist.");
        }
    }

    private void leaveRoom() {
        if (currentRoom != null) {
            out.println("SERVER|Left room: " + currentRoom.getRoomName());
            currentRoom = null;
        }
    }

    private void createRoom(String roomName) {
        ChatRoom room = chatRoomDAO.createRoom(roomName, currentUser.getUserId());
        if (room != null) {
            out.println("SERVER|Room '" + roomName + "' created successfully.");
        } else {
            out.println("SERVER|Failed to create room. Name might be taken.");
        }
    }

    private void listRooms() {
        List<ChatRoom> rooms = chatRoomDAO.getAllRooms();
        out.println("SERVER|--- Available Rooms ---");
        for (ChatRoom r : rooms) {
            out.println("SERVER|- " + r.getRoomName());
        }
    }

    private void listUsers() {
        out.println("SERVER|--- Online Users ---");
        for (String user : ChatServer.onlineUsers.keySet()) {
            out.println("SERVER|- " + user);
        }
    }

    private void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler handler : ChatServer.onlineUsers.values()) {
            if (handler != exclude) {
                handler.sendMessage(message);
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void disconnect() {
        if (currentUser != null) {
            ChatServer.onlineUsers.remove(currentUser.getUsername());
            broadcast("SERVER|User " + currentUser.getUsername() + " has left.", null);
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
