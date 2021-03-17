package com.example.batch.scope;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobParameter {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJobParameter() {
        return jobBuilderFactory.get("simpleJobParameter")
                .start(simpleJobParameterStep1())
                .next(simpleJobParameterStep2(null))
                .build();
    }

    private final SimpleJobTasklet tasklet;

    public Step simpleJobParameterStep1() {
        log.info(">>>>> definition simpleStep1");
        return stepBuilderFactory.get("simpleJobParameterStep1")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    @JobScope
    public Step simpleJobParameterStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleJobParameterStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step2");
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
