package org.parking.manager.service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.openapitools.model.ParkingEntryRequest;
import org.parking.manager.service.entity.CarType;
import org.parking.manager.service.entity.ParkingEvent;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingEventMapperTest {
    private final ParkingEventMapper mapper = Mappers.getMapper(ParkingEventMapper.class);

    @Test
    @DisplayName("toEntity maps ParkingEntryRequest and CarType to ParkingEvent")
    void toEntity_mapsRequestAndCarType() {
        var request = new ParkingEntryRequest("A123BC", "легковой");
        var carType = new CarType();
        carType.setCode("легковой");
        var event = mapper.toEntity(request, carType);

        assertThat(event).isNotNull();
        assertThat(event.getCarNumber()).isEqualTo("A123BC");
        assertThat(event.getCarType()).isEqualTo(carType);
        assertThat(event.getId()).isNull(); // игнорируется
        assertThat(event.getEntryTime()).isNull(); // игнорируется
        assertThat(event.getExitTime()).isNull(); // игнорируется
    }

    @Test
    @DisplayName("toEntryResponse maps ParkingEvent to ParkingEntryResponse")
    void toEntryResponse_mapsEventToResponse() {
        var event = new ParkingEvent();
        event.setCarNumber("A123BC");
        event.setEntryTime(OffsetDateTime.now());

        var response = mapper.toEntryResponse(event);

        assertThat(response).isNotNull();
        assertThat(response.getEntryTime()).isEqualTo(event.getEntryTime());
    }

    @Test
    @DisplayName("toExitResponse maps ParkingEvent to ParkingExitResponse")
    void toExitResponse_mapsEventToResponse() {
        var event = new ParkingEvent();
        event.setCarNumber("A123BC");
        event.setExitTime(OffsetDateTime.now());

        var response = mapper.toExitResponse(event);

        assertThat(response).isNotNull();
        assertThat(response.getExitTime()).isEqualTo(event.getExitTime());
    }
}
