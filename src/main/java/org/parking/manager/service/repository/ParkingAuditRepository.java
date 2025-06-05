package org.parking.manager.service.repository;

import org.parking.manager.service.entity.ParkingAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingAuditRepository extends JpaRepository<ParkingAudit, Long> {}

