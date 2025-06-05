package org.parking.manager.service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parking.manager.service.repository.ParkingEventRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkingReportTasklet implements Tasklet {

    private final ParkingEventRepository repository;

    @Override
    public RepeatStatus execute(
            @NonNull StepContribution contribution,
            @NonNull ChunkContext chunkContext
    ) {
        var params = chunkContext.getStepContext().getJobParameters();
        var start = OffsetDateTime.parse((String) params.get("startDate"));
        var end = OffsetDateTime.parse((String) params.get("endDate"));

        var avgStay = repository.findAverageStayDuration(start, end);
        var visits = repository.countVisitsInPeriod(start, end);

        log.info("Period: {} - {}", start, end);
        log.info("Average parking time (seconds): {}", avgStay);
        log.info("Number of visits: {}", visits);

        return RepeatStatus.FINISHED;
    }
}
