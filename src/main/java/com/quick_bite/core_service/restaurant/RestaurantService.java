package com.quick_bite.core_service.restaurant;

import com.quick_bite.core_service.common.PageResponse;
import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.common.exception.ForbiddenActionException;
import com.quick_bite.core_service.restaurant.dto.CreateRestaurantDto;
import com.quick_bite.core_service.restaurant.dto.RestaurantResponseDto;
import com.quick_bite.core_service.restaurant.dto.UpdateRestaurantDto;
import com.quick_bite.core_service.restaurant.dto.UpdateRestaurantStatusDto;
import com.quick_bite.core_service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    private final CurrentUserService currentUserService;

    public RestaurantResponseDto createRestaurant(CreateRestaurantDto dto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(dto);

        //set restaurant owner
        restaurant.setOwner(this.currentUserService.getCurrentUser());

        //set default status for restaurant
        restaurant.setStatus(RestaurantStatus.PENDING);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toRestaurantResponseDto(savedRestaurant);
    }

    public RestaurantResponseDto updateRestaurant (Long restaurantId, UpdateRestaurantDto dto) {
        // get the restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        // verify that the restaurant owner is the current user
        Long currentUserId = this.currentUserService.getCurrentUser().getId();
        Long restaurantOwnerId = restaurant.getOwner().getId();
        if(!currentUserId.equals(restaurantOwnerId)) {
            throw new ForbiddenActionException();
        }

        // update restaurant
        this.restaurantMapper.updateRestaurant(restaurant, dto);

        Restaurant updatedRestaurant = this.restaurantRepository.save(restaurant);

        return this.restaurantMapper.toRestaurantResponseDto(updatedRestaurant);
    }


    public PageResponse<RestaurantResponseDto> getAllActiveRestaurants(Pageable pageable) {
       Page<Restaurant> pageOfRestaurants = this.restaurantRepository.findByStatus(RestaurantStatus.ACTIVE, pageable);



        PageResponse<RestaurantResponseDto> pageResponse = this.restaurantMapper.toRestaurantResponseDtoPageResponse(pageOfRestaurants);
        System.out.println(pageResponse);
        return pageResponse;
    }

    public RestaurantResponseDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        return restaurantMapper.toRestaurantResponseDto(restaurant);
    }

    public RestaurantResponseDto updateRestaurantStatus(Long restaurantId, UpdateRestaurantStatusDto dto) {
        //get the restaurant
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        // check for admin authority
        if (!this.currentUserService.isAdmin()) {
            throw new ForbiddenActionException();
        }

        // set the status
        restaurant.setStatus(RestaurantStatus.setStatus(dto.status()));

        Restaurant updatedRestaurant = this.restaurantRepository.save(restaurant);

        return this.restaurantMapper.toRestaurantResponseDto(updatedRestaurant);

    }
}
