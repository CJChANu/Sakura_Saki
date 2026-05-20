package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packages")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double discountPercent;
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "package_services",
        joinColumns = @JoinColumn(name = "package_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

    public Package() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Service> getServices() { return services; }
    public void setServices(List<Service> services) { this.services = services; }

    public double getTotalPrice() {
        return services.stream().mapToDouble(Service::getPrice).sum();
    }

    public double getDiscountedPrice() {
        double total = getTotalPrice();
        return total - (total * (discountPercent / 100.0));
    }

    public int getTotalDuration() {
        return services.stream().mapToInt(Service::getDurationMinutes).sum();
    }
}
