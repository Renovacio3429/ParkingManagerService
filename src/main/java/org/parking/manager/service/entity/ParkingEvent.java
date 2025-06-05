package org.parking.manager.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "parking_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "car_number", nullable = false, length = 32)
    private String carNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_type", nullable = false)
    private CarType carType;

    @Column(name = "entry_time", nullable = false)
    private OffsetDateTime entryTime;

    @Column(name = "exit_time")
    private OffsetDateTime exitTime;
}
