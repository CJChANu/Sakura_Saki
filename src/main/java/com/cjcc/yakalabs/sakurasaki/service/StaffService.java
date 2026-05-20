package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class StaffService {
    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<Staff> getAll() {
        return staffRepository.findAll();
    }

    public List<Staff> getActive() {
        return staffRepository.findByActive(true);
    }

    public Staff getById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    public void delete(Long id) {
        staffRepository.deleteById(id);
    }

    public void toggleActive(Long id) {
        staffRepository.findById(id).ifPresent(s -> {
            s.setActive(!s.isActive());
            staffRepository.save(s);
        });
    }
}
