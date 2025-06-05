package org.parking.manager.service.scheduler;

import lombok.RequiredArgsConstructor;
import org.parking.manager.service.service.ParkingReportBatchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingReportJobScheduler {
    private final ParkingReportBatchService parkingReportBatchService;

    @Scheduled(cron = "${parking.batch.report-cron}")
    public void triggerParkingReportJob() {
        parkingReportBatchService.runReportBatch();
    }
}
