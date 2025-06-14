package services.impl;

import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserServiceImpl {

    public  List<User> users = new ArrayList<>(List.of(new User("John", "Doe", "johndoe@example.com", "1234567890"), new User("Kate", "Aye", "katee@example.com", "1234565790")));

    public User getUser(String email) {
        Optional<User> user = users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
        return user.orElse(null);
    }
    public User createUser() {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter your first name: ");
        String firstName = scanner.next();
        System.out.println("Enter your last name: ");
        String lastName = scanner.next();
        System.out.println("Enter your email: ");
        String email = scanner.next();
        System.out.println("Enter your phone number: ");
        String phone = scanner.next();

        if (firstName == null || lastName == null || email ==   null || phone == null) {
            System.out.println("Invalid input. Please provide all required information.");
        }
       Optional<String> usersEmail = users.stream()
                .map(User::getEmail)
                .filter(userEmail -> userEmail.equals(email))
               .findFirst();
        if(usersEmail.isPresent()){
            System.out.println("User already exists");
            return null;
        }
        User user = new User(firstName, lastName, email, phone);
        users.add(user);
        return user;

        }


}
