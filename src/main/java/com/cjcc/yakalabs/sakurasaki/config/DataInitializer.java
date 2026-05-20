package com.cjcc.yakalabs.sakurasaki.config;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           StaffRepository staffRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize default Users
        if (userRepository.count() == 0) {
            System.out.println("Initializing default users in database...");

            // 1. System Admin
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@sakurasaki.com");
            admin.setRole("ROLE_ADMIN");
            admin.setEnabled(true);
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setPhone("0112345678");
            admin.setAdminLevel("CEO");
            userRepository.save(admin);

            // 2. Staff User (matches a staff member credentials for login)
            User staffUser = new User();
            staffUser.setUsername("staff");
            staffUser.setPassword(passwordEncoder.encode("staff123"));
            staffUser.setEmail("staff@sakurasaki.com");
            staffUser.setRole("ROLE_STAFF");
            staffUser.setEnabled(true);
            staffUser.setFirstName("Jane");
            staffUser.setLastName("Doe");
            staffUser.setPhone("0771234567");
            userRepository.save(staffUser);

            // 3. Customer User
            Customer customer = new Customer();
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setEmail("customer@gmail.com");
            customer.setRole("ROLE_USER");
            customer.setEnabled(true);
            customer.setFirstName("Alice");
            customer.setLastName("Smith");
            customer.setPhone("0777654321");
            customer.setAddress("123 Sakura Lane, Colombo");
            customer.setLoyaltyPoints(100);
            userRepository.save(customer);

            System.out.println("Default users initialized successfully.");
        }

        // Initialize default Staff members
        if (staffRepository.count() == 0) {
            System.out.println("Initializing default staff members in database...");

            Stylist stylist = new Stylist("STF001", "Jane Stylist", "0771234567",
                    "staff@sakurasaki.com", "Monday,Tuesday,Wednesday", "09:00-17:00");
            staffRepository.save(stylist);

            Therapist therapist = new Therapist("STF002", "John Therapist", "0777654321",
                    "john@sakurasaki.com", "Thursday,Friday,Saturday", "10:00-18:00");
            staffRepository.save(therapist);

            System.out.println("Default staff members initialized successfully.");
        }
    }
}
