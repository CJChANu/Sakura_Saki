package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    // The username of the permanent super admin that cannot be demoted/deactivated/deleted
    private static final String SUPER_ADMIN_USERNAME = "admin";

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Check if a user is the protected super admin.
     */
    public boolean isSuperAdmin(Long id) {
        return userRepo.findById(id)
                .map(u -> SUPER_ADMIN_USERNAME.equals(u.getUsername()))
                .orElse(false);
    }

    /**
     * Check if a user is the protected super admin by username.
     */
    public boolean isSuperAdmin(String username) {
        return SUPER_ADMIN_USERNAME.equals(username);
    }

    // CREATE — register a new admin user
    public User createAdmin(String username, String password, String email) {
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);
        return userRepo.save(admin);
    }

    // READ — list all admin users (filtered by role, not instanceof)
    public List<User> listAdmins() {
        return userRepo.findByRole("ROLE_ADMIN");
    }

    // READ — find admin by ID
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    // READ — list all users
    public List<User> listAllUsers() {
        return userRepo.findAll();
    }

    // READ — search users by username keyword
    public List<User> searchUsers(String keyword) {
        return userRepo.findByUsernameContainingIgnoreCase(keyword);
    }

    // UPDATE — update admin details (super admin can still be edited for email/username)
    public User updateAdmin(Long id, String username, String email) {
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        if (username != null && !username.isBlank()) {
            existing.setUsername(username);
        }
        if (email != null && !email.isBlank()) {
            existing.setEmail(email);
        }
        return userRepo.save(existing);
    }

    // UPDATE — change user role (protected: cannot demote super admin)
    public User changeRole(Long id, String newRole) {
        if (isSuperAdmin(id)) {
            throw new RuntimeException("The primary admin account cannot be demoted.");
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setRole(newRole);
        return userRepo.save(user);
    }

    // UPDATE — toggle user enabled/disabled (protected: cannot disable super admin)
    public User toggleEnabled(Long id) {
        if (isSuperAdmin(id)) {
            throw new RuntimeException("The primary admin account cannot be disabled.");
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setEnabled(!user.isEnabled());
        return userRepo.save(user);
    }

    // DELETE — deactivate (soft delete) an admin (protected: cannot deactivate super admin)
    public void deactivateAdmin(Long id) {
        if (isSuperAdmin(id)) {
            throw new RuntimeException("The primary admin account cannot be deactivated.");
        }
        User admin = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        admin.setEnabled(false);
        userRepo.save(admin);
    }
}
