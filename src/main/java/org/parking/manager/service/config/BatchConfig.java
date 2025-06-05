package org.parking.manager.service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    @Bean
    public Step parkingReportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ParkingReportTasklet reportTasklet
    ) {
        return new StepBuilder("parkingReportStep", jobRepository)
                .tasklet(reportTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job parkingReportJob(
            JobRepository jobRepository,
            Step parkingReportStep
    ) {
        return new JobBuilder("parkingReportJob", jobRepository)
                .start(parkingReportStep)
                .build();
    }
}
