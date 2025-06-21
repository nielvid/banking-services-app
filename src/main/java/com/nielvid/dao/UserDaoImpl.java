package com.nielvid.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nielvid.config.DatabaseUtil;
import com.nielvid.entities.User;

public class UserDaoImpl implements Dao<User, String> {

    public static final String CREATE_USER = "insert into users (client_id, first_name, last_name, email, phone) values (?, ?, ?, ?, ?) returning id";
    public static final String FIND_USER_BY_EMAIL = "select * from users where email = ?";
    public static final String GET_ALL_USERS = "select * from users";

    @Override
    public Optional<User> findById(String id) {
        // Implementation for finding Integerccount by ID
        return Optional.empty();
    }

    @Override
    public Optional<User> findOne(String id) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement pstatement = connection.prepareStatement(FIND_USER_BY_EMAIL);
            pstatement.setString(1, id);
            ResultSet rs = pstatement.executeQuery();
            Optional<User> user = mapResultSetToUser(rs);
            System.out.println("User found: " + user.orElse(null).getEmail());
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }

    @Override
    public User create(User user) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement pstatement = connection.prepareStatement(CREATE_USER);
            pstatement.setString(1, user.getClientId());
            pstatement.setString(2, user.getFirstName());
            pstatement.setString(3, user.getLastName());
            pstatement.setString(4, user.getEmail());
            pstatement.setString(5, user.getPhone());

            ResultSet rs = pstatement.executeQuery();
            connection.commit();
            System.out.println(rs.next());
            int id = rs.getInt("id");
            System.out.print("User created with ID: " + id);
            rs.close();
            pstatement.close();
            user.setId(id);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }

    @Override
    public void update(User entity, String id) {
        // Implementation for updating an account
    }

    @Override
    public void delete(String  id) {
        // Implementation for deleting an account
    }

    @Override
    public List<User> findAll() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(GET_ALL_USERS);
            List<User> users = mapResultSetToUsers(rs);
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Optional<User> mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        while (rs.next()) {
            user.setId(rs.getInt("id"));
            user.setClientId(rs.getString("client_id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
        }
        return Optional.of(user);
    }

    private List<User> mapResultSetToUsers(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User userObj = new User();
            userObj.setId(rs.getInt("id"));
            userObj.setClientId(rs.getString("client_id"));
            userObj.setFirstName(rs.getString("first_name"));
            userObj.setLastName(rs.getString("last_name"));
            userObj.setEmail(rs.getString("email"));
            userObj.setPhone(rs.getString("phone"));
            users.add(userObj);
        }
        return users;
    }

}
