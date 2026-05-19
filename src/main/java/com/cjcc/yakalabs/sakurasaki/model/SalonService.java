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

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private double price;

    private String description;

    @Column(nullable = false)
    private boolean available = true;

    public SalonService() {}

    public SalonService(String serviceName, String category, int duration,
                        double price, String description, boolean available) {
        this.serviceName = serviceName;
        this.category = category;
        this.duration = duration;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getCategoryDetails() {
        return "General salon service";
    }
}
