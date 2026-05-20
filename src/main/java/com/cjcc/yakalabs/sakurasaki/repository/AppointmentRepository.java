package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ── Filter by cross-member IDs

    /** All appointments for a customer — Member 1 userId */
    List<Appointment> findByUserId(String userId);

    /** All appointments assigned to a staff member — Member 2 staffId */
    List<Appointment> findByStaffId(String staffId);

    /** All appointments using a specific service — Member 3 serviceId */
    List<Appointment> findByServiceId(String serviceId);

    /** All appointments using a specific package — Member 3 packageId */
    List<Appointment> findByPackageId(String packageId);

    // ── Filter by status / date
    List<Appointment> findByStatus(String status);

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByAppointmentDateBetween(LocalDate from, LocalDate to);

    List<Appointment> findByUserIdAndStatus(String userId, String status);

    //  Schedule conflict detection (used by ScheduleValidator) ─

    List<Appointment> findByStaffIdAndAppointmentDateAndStatusNot(String staffId, LocalDate date, String status);

    List<Appointment> findByUserIdAndAppointmentDateAndStatusNot(String userId, LocalDate date, String status);

    long countByAppointmentDate(LocalDate date);

    @Query("SELECT a.appointmentDate, COUNT(a) FROM Appointment a WHERE a.appointmentDate BETWEEN :from AND :to GROUP BY a.appointmentDate ORDER BY a.appointmentDate")
    List<Object[]> countAppointmentsPerDay(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT a.serviceId, COUNT(a) FROM Appointment a WHERE a.serviceId IS NOT NULL AND a.appointmentDate BETWEEN :from AND :to GROUP BY a.serviceId ORDER BY COUNT(a) DESC")
    List<Object[]> countByServiceBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(a.totalAmount), 0) FROM Appointment a WHERE a.status = 'COMPLETED'")
    Double sumTotalRevenue();
}
