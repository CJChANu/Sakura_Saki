package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String comment;
    
    // Status can be: PENDING, APPROVED, REJECTED, HIDDEN
    private String status = "PENDING";
    
    // ReviewType can be: STAFF, SERVICE
    @Column(name = "review_type")
    private String reviewType;
    
    // Replaces the old visible boolean. True if APPROVED.
    private boolean visible = false;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private SalonService service;
    
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Review() {
    }
    
    public Review(Customer customer, Appointment appointment, SalonService service, User staff, String reviewType, int rating, String comment) {
        this.customer = customer;
        this.appointment = appointment;
        this.service = service;
        this.staff = staff;
        this.reviewType = reviewType;
        this.rating = rating;
        this.comment = comment;
        this.status = "PENDING";
        this.visible = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.visible = "APPROVED".equalsIgnoreCase(status);
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public SalonService getService() {
        return service;
    }

    public void setService(SalonService service) {
        this.service = service;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

