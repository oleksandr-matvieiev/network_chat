package Model;


import java.util.HashMap;
import java.util.Map;

public class UserManage {
    private static final Map<String, User> users = new HashMap<>();

    public static boolean register(String username, String password, User.Role role) {
        if (username == null || password == null || role == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        if (users.containsKey(username)) {
            return false;
        }
        String hashedPassword = PasswordUtils.hashPassword(password);
        User newUser = new User(username, hashedPassword, role);
        users.put(username, newUser);
        return true;
    }

    public static User login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        User user = users.get(username);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
}
