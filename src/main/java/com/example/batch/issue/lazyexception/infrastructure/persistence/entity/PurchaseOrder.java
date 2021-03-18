package com.example.batch.issue.lazyexception.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String memo;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Product> productList;

    @Builder
    public PurchaseOrder(String memo, List<Product> productList) {
        this.memo = memo;
        this.productList = productList;
    }
}
