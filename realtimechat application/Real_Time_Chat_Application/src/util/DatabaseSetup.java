package util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void initialize() {
        try (Connection conn = DBConnection.getBaseConnection();
             Statement stmt = conn.createStatement()) {
             
            // Create database
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS real_time_chat_db");
            stmt.executeUpdate("USE real_time_chat_db");
            
            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createUsersTable);
            
            // Create chat_rooms table
            String createRoomsTable = "CREATE TABLE IF NOT EXISTS chat_rooms (" +
                    "room_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "room_name VARCHAR(50) NOT NULL UNIQUE," +
                    "created_by INT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (created_by) REFERENCES users(user_id)" +
                    ")";
            stmt.executeUpdate(createRoomsTable);
            
            // Create messages table
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                    "message_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "sender_id INT NOT NULL," +
                    "recipient_id INT," +
                    "room_id INT," +
                    "message_content TEXT NOT NULL," +
                    "message_type VARCHAR(20) NOT NULL," +
                    "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (sender_id) REFERENCES users(user_id)," +
                    "FOREIGN KEY (recipient_id) REFERENCES users(user_id)," +
                    "FOREIGN KEY (room_id) REFERENCES chat_rooms(room_id)" +
                    ")";
            stmt.executeUpdate(createMessagesTable);
            
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}
