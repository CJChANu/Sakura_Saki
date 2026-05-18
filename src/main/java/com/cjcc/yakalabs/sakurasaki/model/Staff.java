package com.cjcc.yakalabs.sakurasaki.model;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class Staff {

    @Id
    private String staffId;
    private String name;
    private String phone;
    private String email;
    private String workingDays;
    private String timeSlot;
    private boolean active;

    protected Staff() {}

    public Staff(String staffId, String name, String phone,
                 String email, String workingDays, String timeSlot) {
        this.staffId = staffId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.workingDays = workingDays;
        this.timeSlot = timeSlot;
        this.active = true;
    }

    public abstract String getRole();

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWorkingDays() {
        return workingDays; }
    public void setWorkingDays(String workingDays) { this.workingDays = workingDays; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String toFileString() {
        return staffId + "|" + name + "|" + phone + "|" + email
                + "|" + workingDays + "|" + timeSlot + "|" + active
                + "|" + getRole();
    }
}
