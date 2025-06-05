package org.parking.manager.service.delegate;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.ParkingApiDelegate;
import org.openapitools.model.*;
import org.parking.manager.service.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ParkingApiDelegateImpl implements ParkingApiDelegate {

    private final ParkingService parkingService;

    @Override
    public ResponseEntity<ParkingEntryResponse> parkingEntryPOST(ParkingEntryRequest request) {
        var response = parkingService.registerEntry(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ParkingExitResponse> parkingExitPOST(ParkingExitRequest request) {
        var response = parkingService.registerExit(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ParkingReportResponse> parkingReportGET(
            OffsetDateTime startDate, OffsetDateTime endDate) {
        var response = parkingService.getReport(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
