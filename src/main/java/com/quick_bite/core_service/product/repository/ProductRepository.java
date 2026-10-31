package com.quick_bite.core_service.product.repository;

import com.quick_bite.core_service.product.dto.BranchProductResponseDto;
import com.quick_bite.core_service.product.dto.RestaurantProductResponseDto;
import com.quick_bite.core_service.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(
            attributePaths = {"category", "restaurant"}
    )
    Optional<Product> findById(Long productId);

    @Query(value = """
        SELECT p.id AS id,
                p.name AS productName,
               p.description AS productDescription,
               p.image_url AS productImageUrl,
               c.name AS categoryName,
               r.name AS restaurantName
        FROM products p 
        JOIN restaurants r
        ON p.restaurant_id = r.id
        JOIN categories c
        ON p.category_id = c.id
        WHERE r.id = :restaurantId
""",
            nativeQuery = true)
    Page<RestaurantProductResponseDto> findAllProductsByRestaurant(@Param("restaurantId") Long restaurantId, Pageable pageable);


}
