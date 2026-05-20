package com.cjcc.yakalabs.sakurasaki.config;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Seeds the database with sample data for testing.
 * Only inserts data if the tables are empty (safe to run multiple times).
 *
 * Entity hierarchy: User (root) → Customer, Staff (→ Stylist, Therapist), Admin
 * All stored in a single `users` table via SINGLE_TABLE inheritance.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final SalonServiceRepository salonServiceRepo;
    private final StaffRepository staffRepo;
    private final AppointmentRepository appointmentRepo;
    private final ServicePackageRepository packageRepo;
    private final ReviewRepository reviewRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepo,
                           CustomerRepository customerRepo,
                           SalonServiceRepository salonServiceRepo,
                           StaffRepository staffRepo,
                           AppointmentRepository appointmentRepo,
                           ServicePackageRepository packageRepo,
                           ReviewRepository reviewRepo,
                           PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
        this.salonServiceRepo = salonServiceRepo;
        this.staffRepo = staffRepo;
        this.appointmentRepo = appointmentRepo;
        this.packageRepo = packageRepo;
        this.reviewRepo = reviewRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ---- Super Admin (CEO level) ----
        if (userRepo.findByUsername("admin").isEmpty()) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setEmail("admin@sakurasaki.com");
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setRole("ROLE_ADMIN");
            admin.setEnabled(true);
            admin.setAdminLevel("CEO");
            userRepo.save(admin);
            System.out.println("✅ Super Admin created (admin / Admin@123)");
        }

        // ---- Manager Admin ----
        if (userRepo.findByUsername("manager").isEmpty()) {
            Admin mgr = new Admin();
            mgr.setUsername("manager");
            mgr.setPassword(passwordEncoder.encode("Manager@123"));
            mgr.setEmail("manager@sakurasaki.com");
            mgr.setFirstName("Salon");
            mgr.setLastName("Manager");
            mgr.setRole("ROLE_ADMIN");
            mgr.setEnabled(true);
            mgr.setAdminLevel("MANAGER");
            userRepo.save(mgr);
            System.out.println("✅ Manager Admin created (manager / Manager@123)");
        }

        // ---- Sample Customers (Customer extends User — same table) ----
        if (userRepo.findByUsername("sakura").isEmpty()) {
            Customer c1 = new Customer();
            c1.setUsername("sakura");
            c1.setPassword(passwordEncoder.encode("User@123"));
            c1.setEmail("sakura@example.com");
            c1.setFirstName("Sakura");
            c1.setLastName("Tanaka");
            c1.setPhone("0771234567");
            c1.setRole("ROLE_USER");
            c1.setEnabled(true);
            userRepo.save(c1);
        }
        if (userRepo.findByUsername("yuki").isEmpty()) {
            Customer c2 = new Customer();
            c2.setUsername("yuki");
            c2.setPassword(passwordEncoder.encode("User@123"));
            c2.setEmail("yuki@example.com");
            c2.setFirstName("Yuki");
            c2.setLastName("Sato");
            c2.setPhone("0779876543");
            c2.setRole("ROLE_USER");
            c2.setEnabled(true);
            userRepo.save(c2);
        }
        if (userRepo.findByUsername("hana").isEmpty()) {
            Customer c3 = new Customer();
            c3.setUsername("hana");
            c3.setPassword(passwordEncoder.encode("User@123"));
            c3.setEmail("hana@example.com");
            c3.setFirstName("Hana");
            c3.setLastName("Suzuki");
            c3.setPhone("0775551234");
            c3.setRole("ROLE_USER");
            c3.setEnabled(true);
            userRepo.save(c3);
        }
        System.out.println("✅ Sample customers created/verified");

        // ---- Sample Salon Services ----
        if (salonServiceRepo.count() == 0) {
            salonServiceRepo.save(new SalonService("Haircut", "Basic haircut and styling", 1500.00, 30, "Hair"));
            salonServiceRepo.save(new SalonService("Hair Coloring", "Full hair color treatment", 4500.00, 90, "Hair"));
            salonServiceRepo.save(new SalonService("Facial", "Deep cleansing facial", 3000.00, 60, "Skin"));
            salonServiceRepo.save(new SalonService("Manicure", "Nail care and polish", 1200.00, 45, "Nails"));
            salonServiceRepo.save(new SalonService("Massage", "Relaxing full body massage", 5000.00, 60, "Spa"));
            salonServiceRepo.save(new SalonService("Bridal Makeup", "Full bridal makeup package", 8000.00, 120, "Bridal"));
            System.out.println("✅ 6 sample salon services created");
        }

        // ---- Sample Staff (Staff extends User — same table, ROLE_STAFF) ----
        if (staffRepo.count() == 0) {
            Stylist mika = new Stylist();
            mika.setUsername("mika.honda");
            mika.setPassword(passwordEncoder.encode("Staff@123"));
            mika.setEmail("mika@sakurasaki.com");
            mika.setFirstName("Mika");
            mika.setLastName("Honda");
            mika.setPhone("0111234567");
            mika.setRole("ROLE_STAFF");
            mika.setEnabled(true);
            mika.setSpecialization("Hair Cutting & Styling");
            mika.setWorkingDays("MON,TUE,WED,THU,FRI");
            mika.setStartTime(LocalTime.of(9, 0));
            mika.setEndTime(LocalTime.of(17, 0));
            mika.setCertificationLevel("Senior");
            staffRepo.save(mika);

            Stylist koji = new Stylist();
            koji.setUsername("koji.yamamoto");
            koji.setPassword(passwordEncoder.encode("Staff@123"));
            koji.setEmail("koji@sakurasaki.com");
            koji.setFirstName("Koji");
            koji.setLastName("Yamamoto");
            koji.setPhone("0119876543");
            koji.setRole("ROLE_STAFF");
            koji.setEnabled(true);
            koji.setSpecialization("Hair Coloring");
            koji.setWorkingDays("MON,WED,FRI");
            koji.setStartTime(LocalTime.of(10, 0));
            koji.setEndTime(LocalTime.of(18, 0));
            koji.setCertificationLevel("Master");
            staffRepo.save(koji);

            Therapist yui = new Therapist();
            yui.setUsername("yui.nakamura");
            yui.setPassword(passwordEncoder.encode("Staff@123"));
            yui.setEmail("yui@sakurasaki.com");
            yui.setFirstName("Yui");
            yui.setLastName("Nakamura");
            yui.setPhone("0115554321");
            yui.setRole("ROLE_STAFF");
            yui.setEnabled(true);
            yui.setSpecialization("Facials & Skin Care");
            yui.setWorkingDays("TUE,THU,SAT");
            yui.setStartTime(LocalTime.of(9, 0));
            yui.setEndTime(LocalTime.of(16, 0));
            yui.setTherapyType("Aromatherapy");
            staffRepo.save(yui);

            System.out.println("✅ 3 sample staff created (ROLE_STAFF, can login)");
        }

        // Fetch current data for relations
        List<Customer> customers = customerRepo.findAll();
        List<SalonService> services = salonServiceRepo.findAll();
        List<Staff> staffList = staffRepo.findAll();

        // ---- Sample Packages ----
        if (packageRepo.count() == 0 && services.size() >= 3) {
            ServicePackage bridal = new ServicePackage("Bridal Bliss", "Complete bridal preparation package", 15.0);
            Set<SalonService> bridalSvcs = new HashSet<>();
            bridalSvcs.add(services.get(0));
            bridalSvcs.add(services.get(2));
            if (services.size() > 5) bridalSvcs.add(services.get(5));
            bridal.setServices(bridalSvcs);
            packageRepo.save(bridal);

            ServicePackage pamper = new ServicePackage("Pamper Day", "Full day pampering experience", 10.0);
            Set<SalonService> pamperSvcs = new HashSet<>();
            pamperSvcs.add(services.get(2));
            pamperSvcs.add(services.get(3));
            pamperSvcs.add(services.get(4));
            pamper.setServices(pamperSvcs);
            packageRepo.save(pamper);

            System.out.println("✅ 2 sample packages created");
        }

        // ---- Sample Appointments ----
        if (appointmentRepo.count() == 0 && !customers.isEmpty() && !services.isEmpty() && !staffList.isEmpty()) {
            LocalDate today = LocalDate.now();

            appointmentRepo.save(new Appointment(customers.get(0), services.get(0), staffList.get(0), today, LocalTime.of(9, 0)));
            if (customers.size() > 1) {
                appointmentRepo.save(new Appointment(customers.get(1), services.get(1 % services.size()), staffList.get(staffList.size() - 1), today, LocalTime.of(10, 30)));
            }

            // Completed appointments (for reviews)
            Appointment comp1 = new Appointment(customers.get(0), services.get(2 % services.size()), staffList.get(0), today.minusDays(2), LocalTime.of(10, 0));
            comp1.setStatus("COMPLETED");
            appointmentRepo.save(comp1);

            if (customers.size() > 1) {
                Appointment comp2 = new Appointment(customers.get(1), services.get(0), staffList.get(0), today.minusDays(3), LocalTime.of(14, 0));
                comp2.setStatus("COMPLETED");
                appointmentRepo.save(comp2);
            }

            System.out.println("✅ Sample appointments created");
        }

        // ---- Sample Reviews ----
        if (reviewRepo.count() == 0) {
            List<Appointment> completed = appointmentRepo.findByStatus("COMPLETED");
            String[] comments = {
                "Amazing service! The staff was incredibly professional and attentive.",
                "Wonderful experience. Will definitely come back for more treatments!",
                "Best salon in town! Love the ambiance and the quality of service."
            };
            int i = 0;
            for (Appointment a : completed) {
                int rating = 4 + (i % 2); // 4 or 5 stars
                Review r = new Review(a.getCustomer(), a.getService(), a.getStaff(), a, rating, comments[i % comments.length]);
                reviewRepo.save(r);
                i++;
            }
            if (!completed.isEmpty()) System.out.println("✅ Sample reviews created");
        }
    }
}
