package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "duration_minutes")
    private Integer duration;
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean active = true;

    @ManyToMany(mappedBy = "services")
    private List<Package> packages;

    public Service() {}

    public Integer getDurationMinutes() {
        return this.duration;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.duration = durationMinutes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<Package> getPackages() { return packages; }
    public void setPackages(List<Package> packages) { this.packages = packages; }
}