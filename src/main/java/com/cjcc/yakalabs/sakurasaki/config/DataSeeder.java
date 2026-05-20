package com.cjcc.yakalabs.sakurasaki.config;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.model.Package;
import com.cjcc.yakalabs.sakurasaki.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PackageRepository packageRepository;
    private final StaffRepository staffRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, ServiceRepository serviceRepository,
                      PackageRepository packageRepository, StaffRepository staffRepository,
                      ReviewRepository reviewRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.packageRepository = packageRepository;
        this.staffRepository = staffRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        User customerUser = null;

        if (userRepository.count() == 0) {
            // Seed Admin
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setEmail("admin@sakurasaki.com");
            admin.setFirstName("Sakura");
            admin.setLastName("Admin");
            admin.setRole("ROLE_ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);

            // Seed Customer
            Customer cust = new Customer();
            cust.setUsername("customer");
            cust.setPassword(passwordEncoder.encode("Customer@123"));
            cust.setEmail("customer@gmail.com");
            cust.setFirstName("Sonali");
            cust.setLastName("Sharma");
            cust.setPhone("+919876543210");
            cust.setRole("ROLE_USER");
            cust.setEnabled(true);
            customerUser = userRepository.save(cust);
        } else {
            customerUser = userRepository.findByUsername("customer").orElse(null);
        }

        if (serviceRepository.count() == 0) {
            Service s1 = createService("Hair Styling", "Precision cuts and styling", "Hair", 1200.0, 45);
            Service s2 = createService("Skincare & Facials", "Premium rejuvenating facial", "Skin", 2500.0, 60);
            Service s3 = createService("Spa & Massage", "Therapeutic relaxing full body massage", "Spa", 3500.0, 90);
            Service s4 = createService("Nail Art & Care", "Creative manicures and designs", "Nails", 800.0, 30);
            serviceRepository.saveAll(List.of(s1, s2, s3, s4));

            if (packageRepository.count() == 0) {
                Package p = new Package();
                p.setName("Ultimate Wellness Bundle");
                p.setDescription("A combining of premium spa facial and massage treatment.");
                p.setDiscountPercent(15.0);
                p.setServices(List.of(s2, s3));
                packageRepository.save(p);
            }

            if (reviewRepository.count() == 0 && customerUser instanceof Customer) {
                Review r1 = new Review();
                r1.setCustomer((Customer) customerUser);
                r1.setService(s3);
                r1.setRating(5);
                r1.setComment("Absolutely heavenly! The deep tissue massage solved my back pain completely.");
                reviewRepository.save(r1);

                Review r2 = new Review();
                r2.setCustomer((Customer) customerUser);
                r2.setService(s1);
                r2.setRating(4);
                r2.setComment("Wonderful styling service. The master stylist was extremely patient and skilled.");
                reviewRepository.save(r2);
            }
        }

        // Seed 10 Staff members if the database has less than 10 staff members
        if (staffRepository.count() < 10) {
            saveStaffIfAbsent("Hana", "Tanaka", "hana@sakurasaki.com", "9876543201", "Precision Coloring", "MON,TUE,WED,THU,FRI", 9, 17, "STYLIST");
            saveStaffIfAbsent("Yuki", "Sato", "yuki@sakurasaki.com", "9876543202", "Deep Tissue & Shiatsu", "WED,THU,FRI,SAT,SUN", 10, 18, "THERAPIST");
            saveStaffIfAbsent("Kenji", "Takahashi", "kenji@sakurasaki.com", "9876543203", "Precision Haircut & Blowout", "MON,TUE,WED,FRI", 9, 17, "STYLIST");
            saveStaffIfAbsent("Yumi", "Watanabe", "yumi@sakurasaki.com", "9876543204", "Aromatherapy & Organic Facials", "MON,TUE,THU,FRI", 9, 17, "THERAPIST");
            saveStaffIfAbsent("Akira", "Suzuki", "akira@sakurasaki.com", "9876543205", "Japanese Perms & Keratin", "TUE,WED,THU,FRI", 10, 18, "STYLIST");
            saveStaffIfAbsent("Mei", "Sato", "mei@sakurasaki.com", "9876543206", "Hot Stone & Swedish Massage", "TUE,THU,FRI,SAT,SUN", 10, 18, "THERAPIST");
            saveStaffIfAbsent("Takashi", "Tanaka", "takashi@sakurasaki.com", "9876543207", "Hair Treatment & Restructuring", "MON,WED,THU,FRI", 9, 17, "STYLIST");
            saveStaffIfAbsent("Risa", "Kobayashi", "risa@sakurasaki.com", "9876543208", "Reflexology & Head Spa", "MON,TUE,WED,SAT,SUN", 9, 17, "THERAPIST");
            saveStaffIfAbsent("Hiroshi", "Nakamura", "hiroshi@sakurasaki.com", "9876543209", "Men's Grooming & Styling", "MON,TUE,THU,FRI,SAT", 9, 17, "STYLIST");
            saveStaffIfAbsent("Aiko", "Yoshida", "aiko@sakurasaki.com", "9876543210", "Thai Massage & Sports Therapy", "WED,THU,FRI,SAT,SUN", 10, 18, "THERAPIST");
        }

        // Seed additional packages if present
        if (packageRepository.count() <= 1) {
            Service s1 = serviceRepository.findAll().stream().filter(s -> "Hair Styling".equals(s.getName())).findFirst().orElse(null);
            Service s2 = serviceRepository.findAll().stream().filter(s -> "Skincare & Facials".equals(s.getName())).findFirst().orElse(null);
            Service s3 = serviceRepository.findAll().stream().filter(s -> "Spa & Massage".equals(s.getName())).findFirst().orElse(null);
            Service s4 = serviceRepository.findAll().stream().filter(s -> "Nail Art & Care".equals(s.getName())).findFirst().orElse(null);

            if (s1 != null && s2 != null && packageRepository.findAll().stream().noneMatch(p -> "Glow & Style Package".equals(p.getName()))) {
                Package p = new Package();
                p.setName("Glow & Style Package");
                p.setDescription("A premium combination of our signature Hair Styling and Skincare & Facials.");
                p.setDiscountPercent(10.0);
                p.setServices(List.of(s1, s2));
                packageRepository.save(p);
            }

            if (s3 != null && s4 != null && packageRepository.findAll().stream().noneMatch(p -> "Complete Pamper Package".equals(p.getName()))) {
                Package p = new Package();
                p.setName("Complete Pamper Package");
                p.setDescription("Unwind with a therapeutic Spa & Massage and our creative Nail Art & Care.");
                p.setDiscountPercent(12.0);
                p.setServices(List.of(s3, s4));
                packageRepository.save(p);
            }

            if (s1 != null && s4 != null && packageRepository.findAll().stream().noneMatch(p -> "Royal Makeover".equals(p.getName()))) {
                Package p = new Package();
                p.setName("Royal Makeover");
                p.setDescription("Transform yourself with professional Hair Styling and Nail Art & Care.");
                p.setDiscountPercent(8.0);
                p.setServices(List.of(s1, s4));
                packageRepository.save(p);
            }
        }
    }

    private void saveStaffIfAbsent(String first, String last, String email, String phone, String spec, String days, int startHr, int endHr, String type) {
        if (staffRepository.findByEmail(email).isEmpty()) {
            Staff s;
            if ("STYLIST".equals(type)) {
                s = new Stylist();
            } else if ("THERAPIST".equals(type)) {
                s = new Therapist();
            } else {
                s = new GeneralStaff();
            }
            s.setFirstName(first);
            s.setLastName(last);
            s.setEmail(email);
            s.setPhone(phone);
            s.setSpecialization(spec);
            s.setWorkingDays(days);
            s.setStartTime(LocalTime.of(startHr, 0));
            s.setEndTime(LocalTime.of(endHr, 0));
            s.setActive(true);
            staffRepository.save(s);
        }
    }

    private Service createService(String name, String desc, String cat, double price, int duration) {
        Service s = new Service();
        s.setName(name);
        s.setDescription(desc);
        s.setCategory(cat);
        s.setPrice(price);
        s.setDurationMinutes(duration);
        s.setActive(true);
        return s;
    }
}
