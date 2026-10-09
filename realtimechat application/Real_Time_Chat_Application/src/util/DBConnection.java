package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Properties props = new Properties();
    private static boolean initialized = false;

    private static void loadProperties() {
        if (initialized) return;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL ERROR: MySQL JDBC Driver not found in classpath! Please add mysql-connector-j.jar to your Build Path.");
            return;
        }

        java.io.InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
        if (is != null) {
            try {
                props.load(is);
                initialized = true;
                return;
            } catch (Exception e) {}
        }
        
        // Fallback to file system
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
            initialized = true;
        } catch (Exception e) {
            try (FileInputStream fis2 = new FileInputStream("Real_Time_Chat_Application/db.properties")) {
                props.load(fis2);
                initialized = true;
            } catch (Exception ex) {
                System.err.println("Warning: db.properties not found. Using default values.");
                // Set default properties
                props.setProperty("db.host", "localhost");
                props.setProperty("db.port", "3306");
                props.setProperty("db.name", "real_time_chat_db");
                props.setProperty("db.user", "root");
                props.setProperty("db.password", "dharsan@2006");
                initialized = true;
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        loadProperties();
        String url = "jdbc:mysql://" + props.getProperty("db.host", "localhost") + ":" +
                     props.getProperty("db.port", "3306") + "/" +
                     props.getProperty("db.name", "real_time_chat_db") + "?useSSL=false&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, props.getProperty("db.user", "root"), props.getProperty("db.password", "dharsan@2006"));
    }
    
    // For initializing the database itself if it doesn't exist
    public static Connection getBaseConnection() throws SQLException {
        loadProperties();
        String url = "jdbc:mysql://" + props.getProperty("db.host", "localhost") + ":" +
                     props.getProperty("db.port", "3306") + "/?useSSL=false&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, props.getProperty("db.user", "root"), props.getProperty("db.password", "dharsan@2006"));
    }
}
