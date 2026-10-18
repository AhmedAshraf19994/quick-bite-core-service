package com.quick_bite.core_service.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant>  findByStatus(RestaurantStatus status, Pageable pageable);
}
