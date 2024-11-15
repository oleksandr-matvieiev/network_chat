package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Message> messages;

    public ChatHistory() {
        messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        if (message.getMessageType() != Message.MessageType.SYSTEM) {
            messages.add(message);
        }
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public static void cleanHistory() {
        messages.clear();
    }

    public static ChatHistory loadChat(String filename) {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No this file. Creating new file!");
            return new ChatHistory();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (ChatHistory) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while loading file: " + e.getMessage());
            return new ChatHistory();
        }
    }

    public void saveChat(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error while saving chatData: " + e.getMessage());
        }
    }

}
