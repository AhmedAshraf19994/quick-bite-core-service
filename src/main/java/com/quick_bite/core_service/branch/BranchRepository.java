package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.restaurant.Restaurant;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;


public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByRestaurant(Restaurant restaurant);

    @Query(value = """  
            SELECT b.*
            FROM branches b
            Join restaurants r
                    ON b.restaurant_id = r.id
            WHERE b.is_active = true
            And r.status = 'active'
            AND ST_DWithin(b.location, ST_MakePoint(:lng, :lat), b.delivery_radius*1000)
            """,
            nativeQuery = true)
    List<Branch> findByLocation(@Param("lat")BigDecimal lat, @Param("lng") BigDecimal lng);
}
