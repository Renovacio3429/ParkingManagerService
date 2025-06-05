package org.parking.manager.service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.*;
import org.parking.manager.service.annotation.Audited;
import org.parking.manager.service.dictionary.ParkingAuditAction;
import org.parking.manager.service.exception.DuplicateEntryException;
import org.parking.manager.service.exception.ParkingFullException;
import org.parking.manager.service.mapper.ParkingEventMapper;
import org.parking.manager.service.repository.CarTypeRepository;
import org.parking.manager.service.repository.ParkingEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingService {

    @Value("${parking.capacity}")
    private int parkingCapacity;

    private final ParkingEventRepository eventRepository;
    private final CarTypeRepository carTypeRepository;
    private final ParkingEventMapper eventMapper;

    @Transactional
    @Audited(action = ParkingAuditAction.ENTRY_ATTEMPT)
    public ParkingEntryResponse registerEntry(final ParkingEntryRequest request) {
        log.info("Entry attempt: [{}], type [{}]", request.getCarNumber(), request.getCarType());

        if (eventRepository.findByCarNumberAndExitTimeIsNull(request.getCarNumber()).isPresent()) {
            log.warn("Entry denied: [{}] is already in the parking lot", request.getCarNumber());
            throw new DuplicateEntryException("The car is already in the parking lot");
        }

        if (eventRepository.countOccupiedSpaces() >= parkingCapacity) {
            log.warn("Entry denied: parking is full (capacity: {})", parkingCapacity);
            throw new ParkingFullException("Parking is full");
        }

        var carType = carTypeRepository.findById(request.getCarType())
                .orElseThrow(() -> {
                    log.error("Entry denied: unknown car type [{}] for [{}]", request.getCarType(), request.getCarNumber());
                    return new IllegalArgumentException("Unknown car type: " + request.getCarType());
                });

        var event = eventMapper.toEntity(request, carType);
        event.setEntryTime(OffsetDateTime.now());
        eventRepository.save(event);

        log.info("Entry successfully registered: [{}] [{}]", request.getCarNumber(), request.getCarType());
        return eventMapper.toEntryResponse(event);
    }

    @Transactional
    @Audited(action = ParkingAuditAction.EXIT_ATTEMPT)
    public ParkingExitResponse registerExit(final ParkingExitRequest request) {
        log.info("Exit attempt: [{}]", request.getCarNumber());

        var event = eventRepository.findByCarNumberAndExitTimeIsNull(request.getCarNumber())
                .orElseThrow(() -> {
                    log.warn("Exit denied: [{}] not found in the parking lot", request.getCarNumber());
                    return new EntityNotFoundException("Car not found in the parking lot");
                });

        event.setExitTime(OffsetDateTime.now());
        eventRepository.save(event);

        log.info("Exit successfully registered: [{}]", request.getCarNumber());
        return eventMapper.toExitResponse(event);
    }

    public ParkingReportResponse getReport(final OffsetDateTime start, final OffsetDateTime end) {
        var occupied = eventRepository.countOccupiedSpaces();
        var free = parkingCapacity - occupied;
        var avgStaySeconds = eventRepository.findAverageStayDuration(start, end);

        log.info("Parking report: period {} - {}", start, end);
        log.info("Occupied: {}, Free: {}, Average stay: {} min",
                occupied, free, avgStaySeconds != null ? avgStaySeconds / 60.0 : 0.0);

        return new ParkingReportResponse()
                .occupiedSpaces(occupied)
                .freeSpaces(free)
                .avgStayDurationMinutes(avgStaySeconds != null ? avgStaySeconds / 60.0 : 0.0);
    }
}