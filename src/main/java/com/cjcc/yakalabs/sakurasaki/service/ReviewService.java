package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Review;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    public List<Review> getVisible() {
        return reviewRepository.findByVisible(true);
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    public void toggleVisible(Long id) {
        reviewRepository.findById(id).ifPresent(r -> {
            r.setVisible(!r.isVisible());
            reviewRepository.save(r);
        });
    }
}
