package org.parking.manager.service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.parking.manager.service.entity.CarType;
import org.parking.manager.service.entity.ParkingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ParkingEventRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private ParkingEventRepository repository;

    @Autowired
    private CarTypeRepository carTypeRepository;  // Репозиторий для CarType

    private CarType sedanType;

    @BeforeEach
    void setup() {
        // Проверяем и добавляем, если нужно, типы машин
        if (!carTypeRepository.existsById("SEDAN")) {
            carTypeRepository.save(new CarType("SEDAN", "Легковой"));
        }
        if (!carTypeRepository.existsById("TRUCK")) {
            carTypeRepository.save(new CarType("TRUCK", "Грузовой"));
        }

        sedanType = carTypeRepository.findById("SEDAN").orElseThrow();
    }

    @Test
    @DisplayName("findByCarNumberAndExitTimeIsNull returns correct event")
    void findByCarNumberAndExitTimeIsNull() {
        var event = new ParkingEvent();
        event.setCarNumber("ABC123");
        event.setCarType(sedanType);
        event.setEntryTime(OffsetDateTime.now().minusHours(2));
        event.setExitTime(null);
        repository.save(event);

        var found = repository.findByCarNumberAndExitTimeIsNull("ABC123");
        assertThat(found).isPresent();
        assertThat(found.get().getCarNumber()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("countOccupiedSpaces counts only events with null exitTime")
    void countOccupiedSpaces() {
        var ev1 = new ParkingEvent();
        ev1.setCarNumber("AAA111");
        ev1.setCarType(sedanType);
        ev1.setEntryTime(OffsetDateTime.now().minusHours(3));
        ev1.setExitTime(null);
        repository.save(ev1);

        var ev2 = new ParkingEvent();
        ev2.setCarNumber("BBB222");
        ev2.setCarType(sedanType);
        ev2.setEntryTime(OffsetDateTime.now().minusHours(2));
        ev2.setExitTime(OffsetDateTime.now().minusHours(1));
        repository.save(ev2);

        var count = repository.countOccupiedSpaces();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("findAverageStayDuration returns average stay (seconds) between dates")
    void findAverageStayDuration() {
        var start = OffsetDateTime.now().minusDays(2);
        var end = OffsetDateTime.now();

        var ev1 = new ParkingEvent();
        ev1.setCarNumber("CAR1");
        ev1.setCarType(sedanType);
        ev1.setEntryTime(start.plusHours(1));
        ev1.setExitTime(start.plusHours(2));
        repository.save(ev1);

        var ev2 = new ParkingEvent();
        ev2.setCarNumber("CAR2");
        ev2.setCarType(sedanType);
        ev2.setEntryTime(start.plusHours(3));
        ev2.setExitTime(start.plusHours(5));
        repository.save(ev2);

        var avg = repository.findAverageStayDuration(start, end);
        // ev1: 1 hour (3600 sec), ev2: 2 hours (7200 sec), avg = 5400.0
        assertThat(avg).isNotNull();
        assertThat(avg).isBetween(5399.0, 5401.0);
    }

    @Test
    @DisplayName("countVisitsInPeriod counts visits with entry and exit in period")
    void countVisitsInPeriod() {
        var start = OffsetDateTime.now().minusDays(1);
        var end = OffsetDateTime.now().plusDays(1);

        var ev1 = new ParkingEvent();
        ev1.setCarNumber("X1");
        ev1.setCarType(sedanType);
        ev1.setEntryTime(start.plusHours(1));
        ev1.setExitTime(end.minusHours(1));
        repository.save(ev1);

        var ev2 = new ParkingEvent();
        ev2.setCarNumber("X2");
        ev2.setCarType(sedanType);
        ev2.setEntryTime(start.minusDays(2)); // вне периода
        ev2.setExitTime(start.minusDays(2).plusHours(2));
        repository.save(ev2);

        var count = repository.countVisitsInPeriod(start, end);
        assertThat(count).isEqualTo(1);
    }
}
