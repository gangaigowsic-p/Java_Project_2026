package dao;

import model.Message;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean saveMessage(Message message) {
        String sql = "INSERT INTO messages (sender_id, recipient_id, room_id, message_content, message_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, message.getSenderId());
            
            if (message.getRecipientId() != null) {
                pstmt.setInt(2, message.getRecipientId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            if (message.getRoomId() != null) {
                pstmt.setInt(3, message.getRoomId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(4, message.getMessageContent());
            pstmt.setString(5, message.getMessageType());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getRecentMessages(int limit) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.username FROM messages m JOIN users u ON m.sender_id = u.user_id " +
                     "WHERE m.message_type = 'PUBLIC' ORDER BY m.sent_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message msg = mapRowToMessage(rs);
                messages.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Reverse to show chronological order
        java.util.Collections.reverse(messages);
        return messages;
    }
    
    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setMessageId(rs.getInt("message_id"));
        msg.setSenderId(rs.getInt("sender_id"));
        
        int recipientId = rs.getInt("recipient_id");
        if (!rs.wasNull()) msg.setRecipientId(recipientId);
        
        int roomId = rs.getInt("room_id");
        if (!rs.wasNull()) msg.setRoomId(roomId);
        
        msg.setMessageContent(rs.getString("message_content"));
        msg.setMessageType(rs.getString("message_type"));
        msg.setSentAt(rs.getTimestamp("sent_at"));
        
        try {
            msg.setSenderUsername(rs.getString("username"));
        } catch(SQLException e) {
            // username column might not be present in all queries
        }
        
        return msg;
    }
}
