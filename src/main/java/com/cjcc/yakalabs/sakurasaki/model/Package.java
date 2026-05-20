package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;
    private Integer totalDuration;

    @Column(name = "discount_percent")
    private Double discount;

    private Boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "package_services",
            joinColumns = @JoinColumn(name = "package_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;

    public Package() {}

    // Alias method to match CustomerController's expected logic
    public Double getDiscountedPrice() {
        if (this.price != null && this.discount != null) {
            return this.price - (this.price * (this.discount / 100.0));
        }
        return this.price;
    }

    // DataSeeder එකේ error එක නැති කරන්න හදන alias method එකක්
    public void setDiscountPercent(Double discountPercent) {
        this.discount = discountPercent;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getTotalDuration() { return totalDuration; }
    public void setTotalDuration(Integer totalDuration) { this.totalDuration = totalDuration; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<Service> getServices() { return services; }
    public void setServices(List<Service> services) { this.services = services; }
}