package org.example.cleanprj.services;

import org.example.cleanprj.dao.UserDAO;
import org.example.cleanprj.models.User;
import java.io.IOException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public void registerUser(User user) throws IOException {
        if (isEmailRegistered(user.getEmail())) {
            System.out.println("Email already registered");
            throw new IllegalArgumentException("Email already registered");
        } else {
            userDAO.save(user);
        }
    }

    public boolean isEmailRegistered(String email) throws IOException {
        return userDAO.existsByEmail(email);
    }

    public boolean authenticate(String email, String password) throws IOException {
        User user = userDAO.findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }

    public User getUserByEmail(String email) throws IOException {
        return userDAO.findByEmail(email);
    }

    public List<User> getAllUsers() throws IOException {
        return userDAO.findAll();
    }
}
