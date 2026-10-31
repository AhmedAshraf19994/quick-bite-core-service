package com.quick_bite.core_service.product.entity;

import com.quick_bite.core_service.branch.Branch;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branch_product_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BranchProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean isAvailable;

}
