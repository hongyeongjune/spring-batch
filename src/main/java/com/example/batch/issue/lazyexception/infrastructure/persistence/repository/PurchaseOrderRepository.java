package com.example.batch.issue.lazyexception.infrastructure.persistence.repository;

import com.example.batch.issue.lazyexception.infrastructure.persistence.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}
