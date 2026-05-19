package com.cjcc.yakalabs.sakurasaki.util;

import com.cjcc.yakalabs.sakurasaki.model.Review;
import com.cjcc.yakalabs.sakurasaki.model.ServiceReview;
import com.cjcc.yakalabs.sakurasaki.model.StaffReview;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewFileHandler {

    private static final String FILE_PATH = "src/main/resources/data/reviews.txt";

    public List<Review> readAllReviews() {
        List<Review> reviews = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }

            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length < 10) {
                    continue;
                }

                String reviewId = parts[0];
                String appointmentId = parts[1];
                String customerId = parts[2];
                String serviceId = parts[3];
                String staffId = parts[4];
                String reviewType = parts[5];
                int rating = Integer.parseInt(parts[6]);
                String comment = parts[7];
                String status = parts[8];
                String date = parts[9];

                Review review;
                if ("SERVICE".equalsIgnoreCase(reviewType)) {
                    review = new ServiceReview(
                            reviewId, appointmentId, customerId, serviceId, staffId,
                            rating, comment, status, date
                    );
                } else {
                    review = new StaffReview(
                            reviewId, appointmentId, customerId, serviceId, staffId,
                            rating, comment, status, date
                    );
                }

                reviews.add(review);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public void writeAllReviews(List<Review> reviews) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Review review : reviews) {
                writer.write(review.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendReview(Review review) {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(review.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}