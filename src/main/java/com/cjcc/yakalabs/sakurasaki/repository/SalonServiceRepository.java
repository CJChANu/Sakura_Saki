package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
    Optional<SalonService> findByServiceId(String serviceId);
}
