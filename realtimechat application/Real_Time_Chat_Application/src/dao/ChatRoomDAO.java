package dao;

import model.ChatRoom;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomDAO {

    public ChatRoom createRoom(String roomName, int createdBy) {
        String sql = "INSERT INTO chat_rooms (room_name, created_by) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, roomName);
            pstmt.setInt(2, createdBy);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int roomId = generatedKeys.getInt(1);
                        return getRoomById(roomId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating room (might already exist): " + e.getMessage());
        }
        return null;
    }

    public ChatRoom getRoomByName(String roomName) {
        String sql = "SELECT * FROM chat_rooms WHERE room_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToChatRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ChatRoom getRoomById(int roomId) {
        String sql = "SELECT * FROM chat_rooms WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToChatRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<ChatRoom> getAllRooms() {
        List<ChatRoom> rooms = new ArrayList<>();
        String sql = "SELECT * FROM chat_rooms ORDER BY created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(mapRowToChatRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    private ChatRoom mapRowToChatRoom(ResultSet rs) throws SQLException {
        ChatRoom room = new ChatRoom();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomName(rs.getString("room_name"));
        room.setCreatedBy(rs.getInt("created_by"));
        room.setCreatedAt(rs.getTimestamp("created_at"));
        return room;
    }
}
