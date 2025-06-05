package org.parking.manager.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.parking.manager.service.dictionary.ParkingAuditAction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "parking_audit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "car_number", nullable = false, length = 32)
    private String carNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ParkingAuditAction action;

    @Column(name = "message")
    private String message;

    @Column(name = "event_time", nullable = false)
    private OffsetDateTime eventTime;
}
