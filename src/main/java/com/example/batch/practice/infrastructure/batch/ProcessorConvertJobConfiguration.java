package com.example.batch.practice.infrastructure.batch;

import com.example.batch.practice.infrastructure.persistence.entity.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorConvertJobConfiguration {

    public static final String JOB_NAME = "ProcessorConvertBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public static int chunkSize = 1000;

    @Bean
    public Job processorConvertJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(processorConvertStep())
                .build();
    }

    @Bean
    @JobScope
    public Step processorConvertStep() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Teacher, Teacher>chunk(chunkSize)
                .reader(processorConvertReader())
                .processor(processorConvertProcessor())
                .writer(processorConvertWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Teacher> processorConvertReader() {
        return new JpaPagingItemReaderBuilder<Teacher>()
                .name(BEAN_PREFIX+"reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    @Bean
    public ItemProcessor<Teacher, Teacher> processorConvertProcessor() {
        return teacher -> {

            boolean isIgnoreTarget = teacher.getId() % 2 == 0L;
            if(isIgnoreTarget) {
                log.info(">>>>>> Teacher name={}, isIgnoreTarget={}", teacher.getName(), isIgnoreTarget);
                return null;
            }
            return teacher;
        };
    }

    @Bean
    public ItemWriter<Teacher> processorConvertWriter() {
        return items -> {
            for (Teacher item : items) {
                log.info("Teacher Name={}", item.getName());
            }
        };
    }
}
