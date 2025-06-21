package com.nielvid.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
        scanner.close();

        if (firstName == null || lastName == null || email == null || phone == null) {
            System.out.println("Invalid input. Please provide all required information.");
        }
        User userPayload = new User(firstName, lastName, email, phone);
        User userExist = findOneUser(email);
        if (userExist.getEmail() != null) {
            System.out.println("User already exists with email: " + email);
            System.exit(1);
        }else{
            System.out.println("Creating user with email: " + email);
            userPayload = userDaoImpl.create(userPayload);
        }
        System.out.println("User created successfully: " + userPayload.toString());
        return userPayload;

    }

    public User findOneUser(String email) {
        try {
            Optional<User> user = userDaoImpl.findOne(email);
            System.out.println("User found: " + user.orElse(null).getEmail());
            return user.orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }

    public List<User> fetchAllUser() {
            List<User> users = userDaoImpl.findAll();
            return users;
    }
}
