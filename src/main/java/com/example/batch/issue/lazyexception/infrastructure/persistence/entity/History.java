package com.example.batch.issue.lazyexception.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class History {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long purchaseOrderId;

    @Column
    private String productIdList;

    @Builder
    public History(Long purchaseOrderId, List<Product> productIdList) {
        this.purchaseOrderId = purchaseOrderId;
        this.productIdList = productIdList.stream()
                .map(product -> String.valueOf(product.getId()))
                .collect(Collectors.joining(", "));
    }
}
