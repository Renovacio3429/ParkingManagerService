package org.parking.manager.service.repository;

import org.parking.manager.service.entity.ParkingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {

    Optional<ParkingEvent> findByCarNumberAndExitTimeIsNull(String carNumber);

    @Query("SELECT COUNT(pe) FROM ParkingEvent pe WHERE pe.exitTime IS NULL")
    long countOccupiedSpaces();

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (exit_time - entry_time))) FROM parking_events WHERE entry_time >= :startDate AND exit_time <= :endDate", nativeQuery = true)
    Double findAverageStayDuration(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    @Query("""
            SELECT COUNT(pe)
            FROM ParkingEvent pe
            WHERE pe.entryTime >= :startDate
              AND pe.exitTime <= :endDate
            """)
    long countVisitsInPeriod(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);
}
