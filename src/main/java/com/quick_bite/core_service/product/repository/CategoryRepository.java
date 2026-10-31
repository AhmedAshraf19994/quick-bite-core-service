package com.quick_bite.core_service.product.repository;

import com.quick_bite.core_service.product.entity.Category;
import com.quick_bite.core_service.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndRestaurant (String name, Restaurant restaurant);

    List<Category> findAllByRestaurantId(Long restaurantId);
}
