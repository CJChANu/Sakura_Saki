package com.cjcc.yakalabs.sakurasaki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cjcc.yakalabs.sakurasaki.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByStatus(String status);
    
    List<Review> findByCustomer_Id(Long customerId);
    
    List<Review> findByReviewTypeAndService_IdAndStatus(String reviewType, Long serviceId, String status);
    
    List<Review> findByReviewTypeAndStaff_IdAndStatus(String reviewType, Long staffId, String status);
    
    List<Review> findTop6ByVisibleOrderByCreatedAtDesc(boolean visible);
    
    boolean existsByAppointment_AppointmentId(Long appointmentId);
}
