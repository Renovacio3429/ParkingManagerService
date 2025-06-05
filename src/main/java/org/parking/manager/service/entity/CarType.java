package org.parking.manager.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "car_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarType {
    @Id
    @Column(name = "code", length = 32, nullable = false)
    private String code;

    @Column(name = "description", length = 128)
    private String description;
}
