package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Stub repository for SalonService — will be fully implemented by the Service module (Member 3).
 */
@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
}
