package com.quick_bite.core_service.rbac.repository;

import com.quick_bite.core_service.rbac.entity.MemberBranch;
import com.quick_bite.core_service.rbac.entity.RestaurantMember;
import com.quick_bite.core_service.restaurant.Restaurant;
import com.quick_bite.core_service.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantMemberRepository extends JpaRepository<RestaurantMember, Long> {

    boolean existsByUserIdAndRestaurantId( Long userId, Long restaurantId);

    List<RestaurantMember> findByRestaurant(Restaurant restaurant);

    @EntityGraph(attributePaths = {
            "user",
            "role",
            "branches",
            "branches.branch"
    })
   Optional<RestaurantMember> findByIdAndRestaurantId(Long memberId, Long restaurantId);

    @EntityGraph(attributePaths = {
            "role",
            "branches",
            "branches.branch"
    })
    Optional<RestaurantMember> findByUser(User user);
}
