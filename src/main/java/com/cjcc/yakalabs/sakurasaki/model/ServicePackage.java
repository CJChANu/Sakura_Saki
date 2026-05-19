package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

@Entity
@Table(name = "service_packages")
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String packageName;

    // Comma-separated list of included service names
    @Column(nullable = false)
    private String includedServices;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private double discount;

    @Column(nullable = false)
    private double finalPrice;

    private String description;

    public ServicePackage() {}

    public ServicePackage(String packageName, String includedServices,
                          double totalPrice, double discount, String description) {
        this.packageName = packageName;
        this.includedServices = includedServices;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.finalPrice = totalPrice - discount;
        this.description = description;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getIncludedServices() { return includedServices; }
    public void setIncludedServices(String includedServices) { this.includedServices = includedServices; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        this.finalPrice = this.totalPrice - this.discount;
    }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) {
        this.discount = discount;
        this.finalPrice = this.totalPrice - this.discount;
    }

    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
