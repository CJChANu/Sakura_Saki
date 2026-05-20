package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.Stylist;
import com.cjcc.yakalabs.sakurasaki.model.Therapist;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepo;
    private final PasswordEncoder passwordEncoder;

    public StaffService(StaffRepository staffRepo, PasswordEncoder passwordEncoder) {
        this.staffRepo = staffRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create staff — uses polymorphism: creates Stylist or Therapist
     * based on the staffType parameter. Auto-generates username and default password.
     */
    public Staff createStaff(String firstName, String lastName, String email, String phone,
                             String specialization, String staffType,
                             String workingDays, LocalTime startTime, LocalTime endTime) {
        Staff staff;
        switch (staffType != null ? staffType.toUpperCase() : "") {
            case "STYLIST":
                staff = new Stylist();
                break;
            case "THERAPIST":
                staff = new Therapist();
                break;
            default:
                staff = new Staff();
                break;
        }
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setEmail(email);
        staff.setPhone(phone);
        staff.setSpecialization(specialization);
        staff.setRole("ROLE_STAFF");
        staff.setEnabled(true);

        // Auto-generate username from name (e.g. "mika.honda") and default password
        String autoUsername = (firstName + "." + lastName).toLowerCase().replaceAll("[^a-z.]", "");
        staff.setUsername(autoUsername);
        staff.setPassword(passwordEncoder.encode("Staff@123"));

        staff.setWorkingDays(workingDays);
        staff.setStartTime(startTime != null ? startTime : LocalTime.of(9, 0));
        staff.setEndTime(endTime != null ? endTime : LocalTime.of(17, 0));
        return staffRepo.save(staff);
    }

    public List<Staff> findAll() {
        return staffRepo.findAll();
    }

    public List<Staff> findActive() {
        return staffRepo.findByActive(true);
    }

    public Optional<Staff> findById(Long id) {
        return staffRepo.findById(id);
    }

    public List<Staff> searchByName(String keyword) {
        return staffRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    public List<Staff> searchBySpecialty(String keyword) {
        return staffRepo.findBySpecializationContainingIgnoreCase(keyword);
    }

    public Staff updateStaff(Long id, String firstName, String lastName, String email, String phone,
                             String specialization, String workingDays, LocalTime startTime, LocalTime endTime) {
        Staff s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        if (firstName != null && !firstName.isBlank()) s.setFirstName(firstName);
        if (lastName != null && !lastName.isBlank()) s.setLastName(lastName);
        if (email != null && !email.isBlank()) s.setEmail(email);
        if (phone != null) s.setPhone(phone);
        if (specialization != null) s.setSpecialization(specialization);
        if (workingDays != null) s.setWorkingDays(workingDays);
        if (startTime != null) s.setStartTime(startTime);
        if (endTime != null) s.setEndTime(endTime);
        return staffRepo.save(s);
    }

    public void toggleActive(Long id) {
        Staff s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        s.setActive(!s.isActive());
        staffRepo.save(s);
    }

    public void delete(Long id) {
        staffRepo.deleteById(id);
    }
}
