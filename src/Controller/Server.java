package Controller;

import Model.ChatHistory;
import Model.Message;
import Model.User;
import Model.UserManage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final int PORT = 12345;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final Set<ClientHandler> clientHandlers = new HashSet<>();
    private static final UserManage userManage = new UserManage();
    public static ChatHistory chatHistory = new ChatHistory();


    public static void main(String[] args) {
        chatHistory = ChatHistory.loadChat("src/chat_history.dat");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Controller.Server started on port: " + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    logger.info("New client joined: " + clientSocket.getInetAddress().getHostAddress());
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error while accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while starting server: " + e.getMessage());
        }
    }


    //Send messages for  all clients
    public static synchronized void broadcastMessage(Message message, ClientHandler sender) {
        chatHistory.addMessage(message);
        chatHistory.saveChat("src/chat_history.dat");
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != sender) {
                clientHandler.sendMessage(message);
            }
        }
    }

    //remove client from server
    public static synchronized void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public static void getAllUsers() {
        Map<String, User> allUsers = userManage.getAllUsers();
        if (allUsers.isEmpty()) {
            System.out.println("No users registered.");
        } else {
            System.out.println("Registered Users:");
            for (Map.Entry<String, User> entry : allUsers.entrySet()) {
                String username = entry.getKey();
                User user = entry.getValue();
                System.out.println("Username: " + username + ", Role: " + user.getRole());
            }
        }
    }

}

class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private User currentUser;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            UserManage.register("alex", "123", User.Role.ADMIN);
            handleAuthentication();
            for (Message message : Server.chatHistory.getMessages()) {
                out.writeObject(message);
            }

            Message message;
            while ((message = (Message) in.readObject()) != null) {
                if (message.getContent().equalsIgnoreCase("exit")) {
                    break;
                }

                if (currentUser.getRole() == User.Role.ADMIN && message.getContent().equals("/all")) {
                    Server.getAllUsers();
                }
                if (currentUser.getRole() == User.Role.ADMIN && message.getContent().equals("/clear")) {
                    ChatHistory.cleanHistory();
                }
                System.out.println(message);
                Server.broadcastMessage(message, this);

            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error while closing client socket: " + e.getMessage());
            }
        }
        Server.removeClient(this);
        System.out.println("Client left the chat!");
    }

    private void handleAuthentication() throws IOException, ClassNotFoundException {
        boolean authenticated = false;
        while (!authenticated) {
            Message authMessage = (Message) in.readObject();
            String content = authMessage.getContent();

            if (content.startsWith("REGISTER")) {
                String[] parts = content.split(" ");
                if (parts.length == 3) {
                    boolean success = UserManage.register(parts[1], parts[2], User.Role.USER);
                    if (success) {
                        currentUser = new User(parts[1], parts[2], User.Role.USER);
                        out.writeObject(new Message("Server", "Registration successful!", Message.MessageType.SYSTEM));
                        out.writeObject(new Message("Server", currentUser.getUsername(), Message.MessageType.SYSTEM));
                        authenticated = true;
                    } else {
                        out.writeObject(new Message("Server", "Username already taken. Try again.", Message.MessageType.SYSTEM));
                    }
                } else {
                    out.writeObject(new Message("Server", "Invalid registration format. Try again.", Message.MessageType.SYSTEM));
                }
            } else if (content.startsWith("LOGIN")) {
                String[] parts = content.split(" ");
                if (parts.length == 3) {
                    User user = UserManage.login(parts[1], parts[2]);
                    if (user != null) {
                        currentUser = user;
                        out.writeObject(new Message("Server", "Login successful", Message.MessageType.SYSTEM));
                        out.writeObject(new Message("Server", currentUser.getUsername(), Message.MessageType.SYSTEM));
                        authenticated = true;
                    } else {
                        out.writeObject(new Message("Server", "Incorrect password or username. Try again.", Message.MessageType.SYSTEM));
                    }
                } else {
                    out.writeObject(new Message("Server", "Invalid login format. Try again.", Message.MessageType.SYSTEM));
                }
            } else {
                out.writeObject(new Message("Server", "Invalid command. Please register or login.", Message.MessageType.SYSTEM));
            }
        }
    }


    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            System.out.println("Error while sending message: " + e.getMessage());
        }
    }
}
