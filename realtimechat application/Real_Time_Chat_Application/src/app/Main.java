package app;

import dao.MessageDAO;
import dao.UserDAO;
import model.Message;
import model.User;
import util.InputValidator;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static UserDAO userDAO = new UserDAO();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("       REAL TIME CHAT APPLICATION");
            System.out.println("========================================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("\nEnter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    register();
                    break;
                case "2":
                    login();
                    break;
                case "3":
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Username (3-20 alphanumeric): ");
        String username = scanner.nextLine();

        if (!InputValidator.isValidUsername(username)) {
            System.out.println("Invalid username format.");
            return;
        }

        System.out.print("Password (min 4 chars): ");
        String password = scanner.nextLine();

        if (!InputValidator.isValidPassword(password)) {
            System.out.println("Invalid password format.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(InputValidator.hashPassword(password));

        if (userDAO.registerUser(user)) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed. Username might be taken.");
        }
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPasswordHash().equals(InputValidator.hashPassword(password))) {
            System.out.println("Login successful.");

            // Print recent messages
            MessageDAO msgDao = new MessageDAO();
            List<Message> recent = msgDao.getRecentMessages(5);
            if (!recent.isEmpty()) {
                System.out.println("\n--- Recent Public Messages ---");
                for (Message m : recent) {
                    System.out
                            .println("[" + m.getSentAt() + "] " + m.getSenderUsername() + ": " + m.getMessageContent());
                }
            }

            // Start client
            ChatClient client = new ChatClient(username, scanner);
            client.start();
        } else {
            System.out.println("Invalid username or password.");
        }
    }
}
