package Viev;

import Model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Scanner scanner;
    private String clientName;

    public Client(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            scanner = new Scanner(System.in);


            handleAuthentication();

            new Thread(new IncomeMessageHandle()).start();

            while (true) {
                System.out.println("Enter message: ");
                String text = scanner.nextLine();
                Message message = new Message(clientName, text, Message.MessageType.NORMAL);

                out.writeObject(message);
                if (text.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while connecting the server: " + e.getMessage());
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println("Error while closing client: " + e.getMessage());
            }
        }
    }

    private void handleAuthentication() throws IOException, ClassNotFoundException {
        boolean authenticated = false;

        while (!authenticated) {
            System.out.println("Do you want to register or login? (register/login): ");
            String choice = scanner.nextLine();

            if ("register".equalsIgnoreCase(choice)) {
                System.out.println("Enter username: ");
                String username = scanner.nextLine();
                System.out.println("Enter password: ");
                String password = scanner.nextLine();
                out.writeObject(new Message("System", "REGISTER " + username + " " + password, Message.MessageType.SYSTEM));
            } else if ("login".equalsIgnoreCase(choice)) {
                System.out.println("Enter username: ");
                String username = scanner.nextLine();
                System.out.println("Enter password: ");
                String password = scanner.nextLine();
                out.writeObject(new Message("System", "LOGIN " + username + " " + password, Message.MessageType.SYSTEM));
            } else {
                System.out.println("Invalid option. Please enter 'register' or 'login'.");
                continue;
            }

            Message response = (Message) in.readObject();
            System.out.println(response.getContent());

            if (response.getContent().equalsIgnoreCase("Login successful") ||
                    response.getContent().equalsIgnoreCase("Registration successful!")) {
                Message usernameMessage = (Message) in.readObject();
                clientName = usernameMessage.getContent();
                authenticated = true;
            }
        }
    }


    private class IncomeMessageHandle implements Runnable {

        @Override
        public void run() {
            Message messageFromServer;
            try {
                while ((messageFromServer = (Message) in.readObject()) != null) {
                    System.out.println(messageFromServer);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error while reading msg from server: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 12345);
    }
}
