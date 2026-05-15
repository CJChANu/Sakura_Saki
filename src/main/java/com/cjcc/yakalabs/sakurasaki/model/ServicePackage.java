package com.example.beautysalonbookingsystem.model;


public class ServicePackage {
    private String packageId;
    private String packageName;
    private String includedServices;
    private double totalPrice;
    private double discount;
    private double finalPrice;
    private String description;

    public ServicePackage() {
    }

    public ServicePackage(String packageId, String packageName, String includedServices,
                          double totalPrice, double discount, String description) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.includedServices = includedServices;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.finalPrice = totalPrice - discount;
        this.description = description;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIncludedServices() {
        return includedServices;
    }

    public void setIncludedServices(String includedServices) {
        this.includedServices = includedServices;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        this.finalPrice = this.totalPrice - this.discount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        this.finalPrice = this.totalPrice - this.discount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toFileString() {
        return packageId + "|" + packageName + "|" + includedServices + "|" +
                totalPrice + "|" + discount + "|" + finalPrice + "|" + description;
    }
}