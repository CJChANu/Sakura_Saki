package com.cjcc.yakalabs.sakurasaki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cjcc.yakalabs.sakurasaki.model.Review;

import java.util.List;

/**
 * Stub repository for Reviews — will be fully implemented by the Reviews module (Member 5).
 * Provides the minimum interface needed by HomeController.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findTop6ByVisibleOrderByCreatedAtDesc(boolean visible);
}
