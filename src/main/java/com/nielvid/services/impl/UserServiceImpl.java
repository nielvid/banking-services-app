package com.nielvid.services.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.nielvid.config.DatabaseUtil;
import com.nielvid.dao.UserDaoImpl;
import com.nielvid.entities.User;

public class UserServiceImpl {

    UserDaoImpl userDaoImpl;

    public UserServiceImpl() {
       this.userDaoImpl = new UserDaoImpl();
    }


    public User createUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your first name: ");
        String firstName = scanner.next();
        System.out.println("Enter your last name: ");
        String lastName = scanner.next();
        System.out.println("Enter your email: ");
        String email = scanner.next();
        System.out.println("Enter your phone number: ");
        String phone = scanner.next();

        if (firstName == null || lastName == null || email == null || phone == null) {
            System.out.println("Invalid input. Please provide all required information.");
        }
        return new User(firstName, lastName, email, phone);
    }

    public User findOneUser(String id) {
        try {
            Optional<User> user = userDaoImpl.findOne(id);
            System.out.println("User found: " + user.orElse(null).getEmail());
            return user.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean userExist(User user) {
        Optional<User> response = userDaoImpl.userExist(user);
        if(response.isPresent()){
            System.out.println("User already exists: " + response.get().getEmail());
            return true;
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
            return userDaoImpl.findByEmail(email);
    }
    public List<User> fetchAllUser() {
            return  userDaoImpl.findAll();
    }
}
