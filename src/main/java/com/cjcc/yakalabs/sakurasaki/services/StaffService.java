package com.cjcc.yakalabs.sakurasaki.services;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(String id) {
        return staffRepository.findById(id);
    }

    public Staff saveStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public void deleteStaff(String id) {
        staffRepository.deleteById(id);
    }

    public List<Staff> searchByName(String name) {
        return staffRepository.findByNameContainingIgnoreCase(name);
    }
}