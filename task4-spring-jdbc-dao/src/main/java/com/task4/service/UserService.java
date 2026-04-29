package com.task4.service;

import com.task4.dao.UserDao;
import com.task4.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public User createUser(String username) {
        validateUsername(username);
        if (userDao.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username '" + username + "' already exists");
        }
        User user = new User(username);
        return userDao.create(user);
    }


    public Optional<User> getUserById(Long id) {
        validateId(id);
        return userDao.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        validateUsername(username);
        return userDao.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }


    public User updateUser(Long id, String newUsername) {
        validateId(id);
        validateUsername(newUsername);
        
        Optional<User> existingUser = userDao.findById(id);
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User with id '" + id + "' not found");
        }
        
        if (userDao.existsByUsername(newUsername)) {
            Optional<User> userWithNewName = userDao.findByUsername(newUsername);
            if (userWithNewName.isPresent() && !userWithNewName.get().getId().equals(id)) {
                throw new IllegalArgumentException("User with username '" + newUsername + "' already exists");
            }
        }
        
        User user = existingUser.get();
        user.setUsername(newUsername);
        userDao.update(user);
        return user;
    }


    public boolean deleteUserById(Long id) {
        validateId(id);
        return userDao.deleteById(id);
    }

    public boolean deleteUserByUsername(String username) {
        validateUsername(username);
        return userDao.deleteByUsername(username);
    }

    public long deleteAllUsers() {
        long count = userDao.count();
        userDao.deleteAll();
        return count;
    }


    public boolean userExists(Long id) {
        validateId(id);
        return userDao.findById(id).isPresent();
    }

    public boolean userExists(String username) {
        validateUsername(username);
        return userDao.existsByUsername(username);
    }

    public long getUserCount() {
        return userDao.count();
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.length() > 255) {
            throw new IllegalArgumentException("Username too long (max 255 characters)");
        }
    }
}