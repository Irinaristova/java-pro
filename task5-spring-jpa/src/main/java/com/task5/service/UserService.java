package com.task5.service;

import com.task5.entity.User;
import com.task5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(String username) {
        validateUsername(username);
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username '" + username + "' already exists");
        }
        User user = new User(username);
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(String username, String email, String firstName, String lastName, Integer age) {
        validateUsername(username);
        validateEmail(email);
        
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username '" + username + "' already exists");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists");
        }
        
        User user = new User(username, email, firstName, lastName, age);
        return userRepository.save(user);
    }

    @Transactional
    public List<User> createUsers(List<User> users) {
        return userRepository.saveAll(users);
    }

    public Optional<User> getUserById(Long id) {
        validateId(id);
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        validateUsername(username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    public List<User> getInactiveUsers() {
        return userRepository.findByIsActiveFalse();
    }

    public List<User> getUsersOlderThan(Integer age) {
        return userRepository.findByAgeGreaterThan(age);
    }

    public List<User> getUsersByAgeRange(Integer from, Integer to) {
        return userRepository.findByAgeBetween(from, to);
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }
        return userRepository.searchByKeyword(keyword);
    }

    public List<String> getAllActiveUsernames() {
        return userRepository.findAllActiveUsernames();
    }

    public Double getAverageAgeOfActiveUsers() {
        return userRepository.getAverageAgeOfActiveUsers();
    }

    public long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    public long countTotalUsers() {
        return userRepository.count();
    }

    public long countUsersInAgeRange(Integer minAge, Integer maxAge) {
        return userRepository.countUsersInAgeRange(minAge, maxAge);
    }

    public List<User> getTopUsersByAge(Integer minAge, Integer limit) {
        return userRepository.findTopUsersByAge(minAge, limit);
    }

    public Page<User> getActiveUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        return userRepository.findByIsActiveTrue(pageable);
    }

    public Page<User> getUsersByAgeGreaterThanPaginated(Integer age, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("age").descending());
        return userRepository.findByAgeGreaterThan(age, pageable);
    }

    @Transactional
    public User updateUser(Long id, String newUsername) {
        validateId(id);
        validateUsername(newUsername);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id '" + id + "' not found"));
        
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("User with username '" + newUsername + "' already exists");
        }
        
        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserEmail(Long id, String email) {
        validateId(id);
        validateEmail(email);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id '" + id + "' not found"));
        
        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists");
        }
        
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserInfo(Long id, String firstName, String lastName, Integer age) {
        validateId(id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id '" + id + "' not found"));
        
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (age != null && age > 0) user.setAge(age);
        
        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        validateId(id);
        userRepository.deactivateUser(id);
    }

    @Transactional
    public void activateUser(Long id) {
        validateId(id);
        userRepository.activateUser(id);
    }

    @Transactional
    public boolean deleteUserById(Long id) {
        validateId(id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteUserByUsername(String username) {
        validateUsername(username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

    @Transactional
    public int deleteInactiveUsers() {
        return userRepository.deleteInactiveUsers();
    }

    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
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

    private void validateEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}