package pl.coderslab.users;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.utils.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class UserDao {

    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";

    private static final String READ_USER_QUERY =
            "SELECT * FROM users WHERE id = ?";

    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET email = ?, username = ?, password = ? WHERE id = ?";

    private static final String DELETE_USER_QUERY =
            "DELETE FROM users WHERE id = ?";

    private static final String FIND_USER_QUERY =
            "SELECT * FROM users";

    public UserDao() {
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void create(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            insert(conn, CREATE_USER_QUERY, user, user.getUserName(), user.getEmail(), hashPassword(user.getPassword()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static User insert(Connection conn, String query, User user, String... params) {
        try (PreparedStatement preStmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                preStmt.setString(i + 1, params[i]);
            }
            preStmt.executeUpdate();

            ResultSet resultSet = preStmt.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            System.out.println(user.toString());
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User read(int userId) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement preStmt = conn.prepareStatement(READ_USER_QUERY);
            preStmt.setInt(1, userId);
            ResultSet rs = preStmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String username = rs.getString("username");
                String password = rs.getString("password");
                //System.out.println(id + " " + username + " " + email);
                User user = new User();
                user.setId(id);
                user.setUserName(username);
                user.setEmail(email);
                user.setPassword(password);
                System.out.println("Reading of the user was successful.");
                System.out.println(user.toString());
                return user;
            }
            System.out.println("The user with the given ID does not exist");
            return null;
        } catch (SQLException throwables) {
            return null;
        }
    }

    public void update(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement preStmt = conn.prepareStatement(UPDATE_USER_QUERY);
            preStmt.setString(1, user.getEmail());
            preStmt.setString(2, user.getUserName());
            preStmt.setString(3, hashPassword(user.getPassword()));
            preStmt.setInt(4, user.getId());
            preStmt.executeUpdate();
            System.out.println("The data update was successful.");
            System.out.println(user.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int userId) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement preStmt = conn.prepareStatement(DELETE_USER_QUERY);
            preStmt.setInt(1, userId);
            preStmt.executeUpdate();
            System.out.println("User with ID " + userId + " has been removed from the database");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public User[] findAll() {
        User[] users = new User[0];
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement preStmt = conn.prepareStatement(FIND_USER_QUERY);
            ResultSet rs = preStmt.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String username = rs.getString("username");
                String password = rs.getString("password");
                //System.out.println(id + " " + email + " " + username + " " + password + " ");
                User user = new User(id, username, email, password);
                users = Arrays.copyOf(addToArray(user, users), addToArray(user, users).length);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //System.out.println(Arrays.toString(users));
        return users;
    }

    private User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1); // Tworzymy kopię tablicy powiększoną o 1.
        tmpUsers[users.length] = u; // Dodajemy obiekt na ostatniej pozycji.
        return tmpUsers; // Zwracamy nową tablicę.
    }
}
