package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ── Dashboard / Report queries

    /** Count appointments on a specific date (used by DashboardService) */
    long countByAppointmentDate(LocalDate date);

    /** Appointments per day within a date range (used by ReportService) */
    @Query("SELECT a.appointmentDate, COUNT(a) FROM Appointment a " +
           "WHERE a.appointmentDate BETWEEN :from AND :to " +
           "GROUP BY a.appointmentDate ORDER BY a.appointmentDate")
    List<Object[]> countAppointmentsPerDay(@Param("from") LocalDate from,
                                           @Param("to") LocalDate to);

    /** Most booked services within a date range (used by ReportService) */
    @Query("SELECT a.serviceId, COUNT(a) FROM Appointment a " +
           "WHERE a.appointmentDate BETWEEN :from AND :to AND a.serviceId IS NOT NULL " +
           "GROUP BY a.serviceId ORDER BY COUNT(a) DESC")
    List<Object[]> countByServiceBetween(@Param("from") LocalDate from,
                                         @Param("to") LocalDate to);

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

    /**
     * Returns appointments where the requested staff member is already booked
     * within an overlapping time window on the same date.
     *
     * Overlap condition:
     *   existing.start < requested.end  AND  existing.end > requested.start
     *
     * existing.end = existing.appointmentTime + existing.durationMinutes
     * requested.end = requestedStart + requestedDurationMinutes
     *
     * @param staffId               Staff to check
     * @param date                  Date to check
     * @param requestedStart        Start time of the new appointment
     * @param requestedEnd          End time  of the new appointment
     * @param excludeAppointmentId  Pass the current ID when editing (0L for new)
     */
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.staffId = :staffId
          AND a.appointmentDate = :date
          AND a.status NOT IN ('CANCELLED')
          AND a.appointmentId <> :excludeId
          AND a.appointmentTime < :requestedEnd
          AND FUNCTION('ADDTIME', a.appointmentTime,
                       FUNCTION('SEC_TO_TIME', a.durationMinutes * 60))
              > :requestedStart
        """)
    List<Appointment> findStaffConflicts(
            @Param("staffId")       String staffId,
            @Param("date")          LocalDate date,
            @Param("requestedStart") LocalTime requestedStart,
            @Param("requestedEnd")   LocalTime requestedEnd,
            @Param("excludeId")      Long excludeAppointmentId
    );

    /**
     * Returns appointments where the same customer already has an overlapping
     * slot on the same date (prevents double-booking a customer).
     */
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.userId = :userId
          AND a.appointmentDate = :date
          AND a.status NOT IN ('CANCELLED')
          AND a.appointmentId <> :excludeId
          AND a.appointmentTime < :requestedEnd
          AND FUNCTION('ADDTIME', a.appointmentTime,
                       FUNCTION('SEC_TO_TIME', a.durationMinutes * 60))
              > :requestedStart
        """)
    List<Appointment> findCustomerConflicts(
            @Param("userId")        String userId,
            @Param("date")          LocalDate date,
            @Param("requestedStart") LocalTime requestedStart,
            @Param("requestedEnd")   LocalTime requestedEnd,
            @Param("excludeId")      Long excludeAppointmentId
    );
}
