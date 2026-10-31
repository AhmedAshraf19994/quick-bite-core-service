package com.quick_bite.core_service.product.repository;

import com.quick_bite.core_service.product.dto.BranchProductResponseDto;
import com.quick_bite.core_service.product.entity.BranchProductDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BranchProductDetailsRepository extends JpaRepository<BranchProductDetails, Long> {

    @Query(value = """
    SELECT p.id AS productId,
           p.name AS productName,
           p.description AS productDescription,
           p.image_url AS productImageUrl,
           bpd.price AS productPrice,
           bpd.stock AS productStock,
           bpd.is_available AS isAvailable
    FROM branch_product_details bpd 
    JOIN products p 
    ON bpd.product_id = p.id
    WHERE bpd.branch_id = :branchId
""", nativeQuery = true)
    Page<BranchProductResponseDto> findByBranch(@Param("branchId") Long branchId, Pageable pageable);

    @Query(value= """
            SELECT p.id AS poductId,
           p.name AS productName,
           p.description AS productDescription,
           p.image_url AS productImageUrl,
           bpd.price AS productPrice,
           bpd.stock AS productStock,
           bpd.is_available AS isAvailable
            FROM branch_product_details bpd
            JOIN products p 
                On bpd.product_id = p.id
            WHERE bpd.branch_id = :branchId
            AND bpd.product_id = :productId

    """, nativeQuery = true)
    Optional<BranchProductResponseDto> findDetailedProductByBranchIdAndProductId(@Param("branchId") Long branchId,@Param("productId") Long productId);

    @EntityGraph(
            attributePaths = {"product", "branch", "branch.restaurant"}
    )
    Optional<BranchProductDetails> findByBranchIdAndProductId(Long branchId, Long productId);
}
