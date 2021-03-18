package com.example.batch.issue.lazyexception.infrastructure.batch;

import com.example.batch.issue.lazyexception.infrastructure.persistence.entity.History;
import com.example.batch.issue.lazyexception.infrastructure.persistence.entity.PurchaseOrder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = EntityContextConfiguration.JOB_NAME)
public class EntityContextConfiguration {
    public static final String JOB_NAME = "entityContextJob";
    private static final String STEP_NAME = "entityContextStep";

    private EntityManagerFactory entityManagerFactory;
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    public EntityContextConfiguration(EntityManagerFactory entityManagerFactory, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get(STEP_NAME)
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get(STEP_NAME)
                .<PurchaseOrder, History>chunk(100)
                .reader(fixReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    private JpaPagingItemReader<? extends PurchaseOrder> reader() {
        JpaPagingItemReader<PurchaseOrder> reader = new JpaPagingItemReader<>();
        reader.setQueryString("select o from PurchaseOrder o");
        reader.setEntityManagerFactory(entityManagerFactory);

        return reader;
    }
    private JpaPagingItemReader<? extends PurchaseOrder> fixReader() {
        JpaPagingItemReader<PurchaseOrder> reader = new JpaPagingItemReader<>();
        reader.setQueryString("select o from PurchaseOrder o");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(100);
        return reader;
    }

    private ItemProcessor<PurchaseOrder, History> processor() {
        return item -> History.builder()
                .purchaseOrderId(item.getId())
                .productIdList(item.getProductList())
                .build();
    }

    private JpaItemWriter<History> writer() {
        JpaItemWriter<History> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
