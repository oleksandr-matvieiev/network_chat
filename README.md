# Chat Application

## Overview
This is a Java-based chat application built with socket programming. It supports multiple clients, authentication (registration and login), and maintains a chat history. The application implements a simple client-server architecture using Object Streams for communication.

---

## Features
- **Authentication**: Users can register and log in.
- **Role Management**: 
  - `ADMIN`: Can view all users and clear chat history.
  - `USER`: Standard user privileges.
- **Chat Functionality**:
  - Real-time messaging between clients.
  - Server broadcasts messages to all clients except the sender.
- **Chat History**: 
  - Messages are saved to a file (`chat_history.dat`) and loaded when the server starts.
- **Secure Password Storage**: Passwords are hashed using SHA-256.
- **Multi-threaded Server**: Handles multiple clients simultaneously.

---

## Project Structure
### **Packages**:
1. `Model`:
   - **Message**: Represents messages sent between clients and the server.
   - **User**: Represents a user with roles (`ADMIN` or `USER`).
   - **UserManage**: Manages user registration and login.
   - **PasswordUtils**: Handles password hashing and verification.
   - **ChatHistory**: Manages chat history (saving, loading, clearing).
2. `View`:
   - **Client**: The client-side implementation for user interaction.
3. `Controller`:
   - **Server**: Manages client connections and message broadcasting.

---

## How to Run

### **Prerequisites**
- Java 8 or later
- IntelliJ IDEA or any IDE of your choice

### **Steps**
1. Clone the repository:
   ```bash
   git clone https://github.com/oleksandr-matvieiev/network_chat
   ```
2. Open the project in IntelliJ IDEA.
3. Run the server:
   - Navigate to ```Controller/Server.java```.
   - Run the ```main``` method.
4. Run the client:
  - Navigate to ```View/Client.java```.
  - Run the ```main``` method.
  - Use the following credentials or create a new account:
    - Default Admin Credentials:
      - Username: ```user```
      - Password: ```123```
---
## Usage
### Server Commands:
- ```/all```: Display all registered users (Admin only).
- ```/clear```: Clear the chat history (Admin only).
### Client Commands:
Type a message and press Enter to send.
Type ```exit``` to leave the chat.

---
## Example Interaction
1. **Server logs**
   ```
       New client joined: 127.0.0.1
       Admin alex logged in.
       Message from user1: Hello!
   ```
2. **Client output:**
   ```
    Do you want to register or login? (register/login): login
    Enter username: alex
    Enter password: 123
    Login successful
    Enter message: Hello everyone!
   ```
   ---
   ## Technologies Used
   - **Java SE**: Core features, Socket programming, Object Streams.
   - **Serialization**:  For message and chat history persistence.
   - **SHA-256**: Secure password hashing
   
