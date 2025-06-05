package org.parking.manager.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapitools.model.ParkingEntryRequest;
import org.openapitools.model.ParkingEntryResponse;
import org.openapitools.model.ParkingExitRequest;
import org.openapitools.model.ParkingExitResponse;
import org.parking.manager.service.entity.CarType;
import org.parking.manager.service.entity.ParkingEvent;
import org.parking.manager.service.exception.DuplicateEntryException;
import org.parking.manager.service.exception.ParkingFullException;
import org.parking.manager.service.mapper.ParkingEventMapper;
import org.parking.manager.service.repository.CarTypeRepository;
import org.parking.manager.service.repository.ParkingEventRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ParkingServiceTest {
    @InjectMocks
    private ParkingService service;

    @Mock
    private ParkingEventRepository eventRepository;

    @Mock
    private CarTypeRepository carTypeRepository;

    @Mock
    private ParkingEventMapper eventMapper;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        closeable = MockitoAnnotations.openMocks(this);

        var field = ParkingService.class.getDeclaredField("parkingCapacity"); // задаем максимальную вместимость для теста
        field.setAccessible(true);
        field.set(service, 10); // устанавливаем значение приватного поля
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void registerEntry_shouldRegisterCarSuccessfully() {
        // Arrange
        var request = new ParkingEntryRequest("A123BC", "легковой");
        var carType = new CarType();
        var event = new ParkingEvent();
        var response = new ParkingEntryResponse();

        when(eventRepository.findByCarNumberAndExitTimeIsNull("A123BC")).thenReturn(Optional.empty());
        when(eventRepository.countOccupiedSpaces()).thenReturn(5L);
        when(carTypeRepository.findById("легковой")).thenReturn(Optional.of(carType));
        when(eventMapper.toEntity(request, carType)).thenReturn(event);
        when(eventMapper.toEntryResponse(event)).thenReturn(response);

        // Act
        var result = service.registerEntry(request);

        // Assert
        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void registerEntry_shouldThrowDuplicateEntryException_whenCarAlreadyOnParking() {
        var request = new ParkingEntryRequest("A123BC", "легковой");
        when(eventRepository.findByCarNumberAndExitTimeIsNull("A123BC"))
                .thenReturn(Optional.of(new ParkingEvent()));

        assertThrows(DuplicateEntryException.class, () -> service.registerEntry(request));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void registerEntry_shouldThrowParkingFullException_whenParkingIsFull() {
        var request = new ParkingEntryRequest("A123BC", "легковой");
        when(eventRepository.findByCarNumberAndExitTimeIsNull("A123BC")).thenReturn(Optional.empty());
        when(eventRepository.countOccupiedSpaces()).thenReturn(10L);

        assertThrows(ParkingFullException.class, () -> service.registerEntry(request));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void registerExit_shouldRegisterExitSuccessfully() {
        var request = new ParkingExitRequest("A123BC");
        var event = new ParkingEvent();
        var response = new ParkingExitResponse();

        when(eventRepository.findByCarNumberAndExitTimeIsNull("A123BC")).thenReturn(Optional.of(event));
        when(eventMapper.toExitResponse(event)).thenReturn(response);

        var result = service.registerExit(request);

        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void registerExit_shouldThrowEntityNotFoundException_whenCarNotOnParking() {
        var request = new ParkingExitRequest("A123BC");
        when(eventRepository.findByCarNumberAndExitTimeIsNull("A123BC")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.registerExit(request));
        verify(eventRepository, never()).save(any());
    }


    @Test
    void getReport_shouldReturnCorrectStats() {
        var start = OffsetDateTime.now().minusDays(1);
        var end = OffsetDateTime.now();

        when(eventRepository.countOccupiedSpaces()).thenReturn(6L);
        when(eventRepository.findAverageStayDuration(start, end)).thenReturn(3600.0); // 1 час в секундах

        var response = service.getReport(start, end);

        assertEquals(6, response.getOccupiedSpaces());
        assertEquals(4, response.getFreeSpaces());
        assertEquals(60.0, response.getAvgStayDurationMinutes());
    }
}
