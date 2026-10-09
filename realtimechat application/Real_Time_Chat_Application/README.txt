========================================
       REAL TIME CHAT APPLICATION
========================================

## 1. Prerequisites
- JDK 17 or higher
- MySQL 8.0 or higher
- MySQL Connector/J (JDBC driver)

## 2. Setup MySQL and Database Configuration
1. Open `db.properties` in the project root.
2. Update `db.password` with your actual MySQL root password.
3. Ensure your MySQL server is running on port 3306 (or change it in db.properties).
4. The application will automatically create the database (`real_time_chat_db`) and tables when the server is first started.

## 3. MySQL Connector/J Installation
1. Download the MySQL Connector/J (e.g., `mysql-connector-j-8.x.x.jar`) from the official MySQL website.
2. Rename it to `mysql-connector-j.jar` and place it in the `lib` folder inside this project directory.
   - If the `lib` folder doesn't exist, create it.

## 4. How to Import into Eclipse
1. Open Eclipse.
2. Go to `File` -> `Import...`
3. Select `General` -> `Existing Projects into Workspace` and click `Next`.
4. Choose `Select root directory` and browse to the `Real_Time_Chat_Application` folder.
5. Make sure the project is checked and click `Finish`.
6. Ensure the `mysql-connector-j.jar` is in the `lib` folder. Right-click the project -> `Refresh`.
7. The `.classpath` file should automatically add it to your Build Path. (If not, Right-click project -> `Build Path` -> `Configure Build Path` -> `Libraries` -> `Add JARs...` and select the jar from the `lib` folder).

## 5. How to Run the Application
You can run this via Eclipse or using the provided Windows Batch scripts.

Using Windows Scripts:
1. Double-click `run.bat`.
2. Choose Option `1` to start the Server. (A new terminal window will open).
3. Wait for "Chat server started on port 5000."
4. Double-click `run.bat` again.
5. Choose Option `2` to start a Client. (Another terminal window opens).
6. You can start multiple clients by repeating step 4-5.

Using Eclipse:
1. Right-click `ChatServer.java` (in `src/app`) -> `Run As` -> `Java Application`.
2. Right-click `Main.java` (in `src/app`) -> `Run As` -> `Java Application`.
3. To open multiple clients in Eclipse, you might need to pin the console view and open a new console view, or just run the compiled client from the terminal using the `.bat` scripts.

## 6. Features and Testing
1. Register a new user.
2. Login with the registered user.
3. Use the menu to Join Public Chat, Send Private Messages, Create/Join Rooms, etc.
4. Open a second client, register a different user, and test chatting back and forth.
5. Disconnect a client (Type `/quit` or choose Logout), the server and other clients will remain active.
6. Messages are persisted in the database.
