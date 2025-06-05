package org.parking.manager.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkingReportBatchService {

    private final JobLauncher jobLauncher;
    private final Job parkingReportJob;

    public void runReportBatch() {
        try {
            jobLauncher.run(parkingReportJob, new JobParametersBuilder()
                    .addString("startDate", OffsetDateTime.now().minusDays(30).toString())
                    .addString("endDate", OffsetDateTime.now().toString())
                    .toJobParameters());
            log.info("Batch job 'parkingReportJob' started successfully.");
        } catch (Exception ex) {
            log.error("Error while starting batch job 'parkingReportJob': {}", ex.getMessage(), ex);
        }
    }
}
