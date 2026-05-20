package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Validation patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new customer user with full validation.
     */
    public User registerNewUser(String username, String rawPassword, String email,
                                      String firstName, String lastName, String phone, String role) {
        // Username validation
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new RuntimeException("Username must be 3-30 characters, using only letters, numbers, and underscores.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username '" + username + "' is already taken. Please choose another.");
        }

        // Email validation
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Please enter a valid email address (e.g., name@example.com).");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists. Please use a different email or sign in.");
        }

        // Password validation
        if (rawPassword == null || !PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new RuntimeException("Password must be at least 8 characters with uppercase, lowercase, digit, and special character (@#$%^&+=!).");
        }

        // Phone validation
        if (phone == null || phone.isBlank()) {
            throw new RuntimeException("Phone number is required.");
        }
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new RuntimeException("Please enter a valid phone number.");
        }

        // Name validation
        if (firstName == null || firstName.isBlank()) {
            throw new RuntimeException("First name is required.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new RuntimeException("Last name is required.");
        }

        User user;
        if ("ADMIN".equalsIgnoreCase(role)) {
            com.cjcc.yakalabs.sakurasaki.model.Admin admin = new com.cjcc.yakalabs.sakurasaki.model.Admin();
            admin.setAdminLevel("ADMIN");
            user = admin;
        } else if ("STAFF".equalsIgnoreCase(role)) {
            user = new User(); // Or a separate StaffUser if you had one, but User is fine
            user.setRole("ROLE_STAFF");
        } else {
            Customer customer = new Customer();
            customer.setPhone(cleanPhone);
            user = customer;
            user.setRole("ROLE_USER");
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        if ("ADMIN".equalsIgnoreCase(role)) {
            user.setRole("ROLE_ADMIN");
        }
        user.setEnabled(true);
        return userRepository.save(user);
    }

    /**
     * Legacy method for creating plain User (admin use).
     */
    public User registerUser(String username, String rawPassword, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void makeAdmin(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setRole("ROLE_ADMIN");
            userRepository.save(user);
        });
    }
}