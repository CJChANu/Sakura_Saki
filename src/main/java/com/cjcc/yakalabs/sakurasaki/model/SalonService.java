package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

@Entity
@Table(name = "salon_services")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "service_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("GENERAL")
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", unique = true, length = 50)
    private String serviceId;

    @Column(name = "service_name")
    private String serviceName;

    private String category;
    private int duration;
    private double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean available = true;

    public SalonService() {}

    public SalonService(String serviceId, String serviceName, String category, int duration,
                        double price, String description, boolean available) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.category = category;
        this.duration = duration;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    // --- Database-specific ID ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // --- Service branch fields ---
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    // --- develop branch Compatibility Getters/Setters ---
    public String getName() { return serviceName; }
    public void setName(String name) { this.serviceName = name; }

    public int getDurationMinutes() { return duration; }
    public void setDurationMinutes(int durationMinutes) { this.duration = durationMinutes; }

    public boolean isActive() { return available; }
    public void setActive(boolean active) { this.available = active; }

    // --- Utility Methods ---
    public String getCategoryDetails() {
        return "General salon service";
    }

    public String toFileString() {
        return serviceId + "|" + serviceName + "|" + category + "|" + duration + "|" +
                price + "|" + description + "|" + available;
    }
}
