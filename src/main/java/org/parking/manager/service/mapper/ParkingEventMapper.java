package org.parking.manager.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.model.ParkingEntryRequest;
import org.openapitools.model.ParkingEntryResponse;
import org.openapitools.model.ParkingExitResponse;
import org.parking.manager.service.entity.CarType;
import org.parking.manager.service.entity.ParkingEvent;

@Mapper(componentModel = "spring")
public interface ParkingEventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "entryTime", ignore = true)
    @Mapping(target = "exitTime", ignore = true)
    @Mapping(target = "carType", source = "carType")
    ParkingEvent toEntity(ParkingEntryRequest request, CarType carType);

    @Mapping(target = "entryTime", source = "entryTime")
    ParkingEntryResponse toEntryResponse(ParkingEvent event);

    @Mapping(target = "exitTime", source = "exitTime")
    ParkingExitResponse toExitResponse(ParkingEvent event);
}
