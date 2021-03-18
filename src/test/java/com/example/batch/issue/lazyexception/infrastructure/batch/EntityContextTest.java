package com.example.batch.issue.lazyexception.infrastructure.batch;

import com.example.batch.issue.lazyexception.infrastructure.persistence.entity.Product;
import com.example.batch.issue.lazyexception.infrastructure.persistence.entity.PurchaseOrder;
import com.example.batch.issue.lazyexception.infrastructure.persistence.repository.PurchaseOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestJobConfig.class, EntityContextConfiguration.class})
@TestPropertySource(properties = {"job.name="+ EntityContextConfiguration.JOB_NAME})
public class EntityContextTest {


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    public void reader에서_processor로_entity넘기면_영속성이_유지되어야한다() throws Exception {

        //given
        for(int i=0; i<100; i++) {
            purchaseOrderRepository.save(
                    PurchaseOrder.builder()
                            .memo("도착할 때 전화주세요.")
                            .productList(
                                    Arrays.asList(
                                            Product.builder().name("마우스").amount(10000L).build(),
                                            Product.builder().name("키보드").amount(30000L).build()
                                    )
                            )
                            .build()
            );
        }

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertEquals(purchaseOrderRepository.findAll().size(), 100);
        assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);

    }
}
