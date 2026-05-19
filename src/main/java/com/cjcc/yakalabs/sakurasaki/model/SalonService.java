package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

/**
 * Stub entity for SalonService — will be fully implemented by the Service module (Member 3).
 * Provides the minimum structure needed for Review entity and DashboardService to compile.
 */
@Entity
@Table(name = "salon_services")
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private int durationMinutes;
    private boolean active = true;

    public SalonService() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
