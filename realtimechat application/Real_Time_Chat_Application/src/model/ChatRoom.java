package model;

import java.sql.Timestamp;

public class ChatRoom {
    private int roomId;
    private String roomName;
    private int createdBy;
    private Timestamp createdAt;

    public ChatRoom() {}

    public ChatRoom(int roomId, String roomName, int createdBy, Timestamp createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
