package Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 2L;

    private String sender;
    private String content;
    private final LocalDateTime sendTime;
    private MessageType messageType;


    /*
     * Represents a message sent in a chat application. Each message contains the sender,
     * the content of the message, the time it was sent, and the type of message.
     */
    public Message(String sender, String content, MessageType messageType) {
        if (sender == null || content == null || messageType == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        this.sender = sender;
        this.content = content;
        this.sendTime = LocalDateTime.now();
        this.messageType = messageType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        if (messageType == null) {
            throw new IllegalArgumentException("MessageType cannot be null");
        }
        this.messageType = messageType;
    }

    public enum MessageType {
        SYSTEM,
        NORMAL
    }

    @Override
    public String toString() {
        return sender + ": " + content;
    }

}
