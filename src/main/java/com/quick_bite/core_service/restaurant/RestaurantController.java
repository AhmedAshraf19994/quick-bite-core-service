package com.quick_bite.core_service.restaurant;

import com.quick_bite.core_service.common.PageResponse;
import com.quick_bite.core_service.common.Response;
import com.quick_bite.core_service.restaurant.dto.CreateRestaurantDto;
import com.quick_bite.core_service.restaurant.dto.RestaurantResponseDto;
import com.quick_bite.core_service.restaurant.dto.UpdateRestaurantDto;
import com.quick_bite.core_service.restaurant.dto.UpdateRestaurantStatusDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<RestaurantResponseDto> createRestaurant(@Valid @RequestBody CreateRestaurantDto dto) {
        RestaurantResponseDto restaurantResponseDto = restaurantService.createRestaurant(dto);
        return new Response<>(
                true,
                HttpStatus.CREATED.value(),
                "Restaurant created successfully",
                restaurantResponseDto
        );
    }

    @PatchMapping("/{restaurantId}")
    public Response<RestaurantResponseDto> updateRestaurant(
            @PathVariable Long restaurantId,
            @Valid @RequestBody UpdateRestaurantDto dto
    ) {
        RestaurantResponseDto restaurantResponseDto = restaurantService.updateRestaurant(restaurantId, dto);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Restaurant updated successfully",
                restaurantResponseDto
        );
    }

    @GetMapping
    public Response<PageResponse<RestaurantResponseDto>> getAllActiveRestaurants(Pageable pageable)
     {
        PageResponse<RestaurantResponseDto> restaurantsPage = restaurantService.getAllActiveRestaurants(pageable);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Restaurants retrieved successfully",
                restaurantsPage
        );
    }

    @GetMapping("/{restaurantId}")
    public Response<RestaurantResponseDto> getRestaurantById(@PathVariable("restaurantId") Long restaurantId) {
        RestaurantResponseDto restaurant = this.restaurantService.getRestaurantById(restaurantId);

        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Restaurant retrieved successfully",
                restaurant
        );
    }

    @PatchMapping("/{restaurantId}/status")
    public Response<RestaurantResponseDto> updateRestaurantStatus(@PathVariable Long restaurantId, @RequestBody UpdateRestaurantStatusDto dto) {
        RestaurantResponseDto restaurant = restaurantService.updateRestaurantStatus(restaurantId, dto);

        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Restaurant status updated successfully",
                restaurant
        );
    }

    }
