package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByActive(boolean active);
    Optional<Staff> findByEmail(String email);
}
