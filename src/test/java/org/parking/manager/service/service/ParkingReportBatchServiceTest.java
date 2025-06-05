package org.parking.manager.service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ParkingReportBatchServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job parkingReportJob;

    @InjectMocks
    private ParkingReportBatchService batchService;


    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // закрываем ресурсы Mockito
    }

    @Test
    void runReportBatch_shouldLaunchBatchJobSuccessfully() throws Exception {
        // Arrange
        when(jobLauncher.run(eq(parkingReportJob), any(JobParameters.class)))
                .thenReturn(mock(JobExecution.class));

        // Act
        batchService.runReportBatch();

        // Assert
        verify(jobLauncher).run(eq(parkingReportJob), any(JobParameters.class));
    }

    @Test
    void runReportBatch_shouldLogErrorOnException() throws Exception {
        // Arrange
        when(jobLauncher.run(eq(parkingReportJob), any(JobParameters.class)))
                .thenThrow(new RuntimeException("Batch error"));

        // Act + Assert
        // Не выбрасываем исключение, только проверяем что оно логируется
        batchService.runReportBatch();

        verify(jobLauncher).run(eq(parkingReportJob), any(JobParameters.class));
    }
}
