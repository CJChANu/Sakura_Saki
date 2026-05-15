package com.cjcc.yakalabs.sakurasaki.model;


public class SalonService {
    private String serviceId;
    private String serviceName;
    private String category;
    private int duration;
    private double price;
    private String description;
    private boolean available;

    public SalonService() {
    }

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCategoryDetails() {
        return "General salon service";
    }

    public String toFileString() {
        return serviceId + "|" + serviceName + "|" + category + "|" + duration + "|" +
                price + "|" + description + "|" + available;
    }
}