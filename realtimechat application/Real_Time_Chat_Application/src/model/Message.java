package model;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int senderId;
    private Integer recipientId; // Nullable for non-private
    private Integer roomId;      // Nullable for non-room
    private String messageContent;
    private String messageType; // PUBLIC, PRIVATE, ROOM
    private Timestamp sentAt;
    
    // Additional helpful field for display
    private String senderUsername;

    public Message() {}

    public Message(int messageId, int senderId, Integer recipientId, Integer roomId, String messageContent, String messageType, Timestamp sentAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.roomId = roomId;
        this.messageContent = messageContent;
        this.messageType = messageType;
        this.sentAt = sentAt;
    }

    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public Integer getRecipientId() { return recipientId; }
    public void setRecipientId(Integer recipientId) { this.recipientId = recipientId; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
}
