package org.parking.manager.service.repository;


import org.parking.manager.service.entity.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarTypeRepository extends JpaRepository<CarType, String> {}
