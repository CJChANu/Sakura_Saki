package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByActive(boolean active);
    List<Service> findByCategoryAndActive(String category, boolean active);
}
