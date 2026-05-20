package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("NAIL")
public class NailService extends SalonService {
    private String nailStyle;

    public NailService() {
        super();
    }

    public NailService(String serviceId, String serviceName, int duration, double price,
                       String description, boolean available, String nailStyle) {
        super(serviceId, serviceName, "Nail", duration, price, description, available);
        this.nailStyle = nailStyle;
    }

    public String getNailStyle() {
        return nailStyle;
    }

    public void setNailStyle(String nailStyle) {
        this.nailStyle = nailStyle;
    }

    @Override
    public String getCategoryDetails() {
        return "Nail service with style: " + nailStyle;
    }
}
