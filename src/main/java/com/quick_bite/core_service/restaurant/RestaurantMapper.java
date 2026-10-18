package com.quick_bite.core_service.restaurant;


import com.quick_bite.core_service.common.PageResponse;
import com.quick_bite.core_service.restaurant.dto.CreateRestaurantDto;
import com.quick_bite.core_service.restaurant.dto.RestaurantResponseDto;
import com.quick_bite.core_service.restaurant.dto.UpdateRestaurantDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE
)
public interface RestaurantMapper {

    @Mapping(target="id", ignore=true)
    @Mapping(target="createdAt", ignore=true)
    @Mapping(target="updatedAt", ignore=true)
    @Mapping(target="statusUpdatedAt", ignore=true)
    @Mapping(target="status",ignore = true)
    @Mapping(target = "owner", ignore = true)
    Restaurant toRestaurant(CreateRestaurantDto dto);

    RestaurantResponseDto toRestaurantResponseDto(Restaurant restaurant);

    void updateRestaurant(@MappingTarget Restaurant restaurant, UpdateRestaurantDto dto);

    PageResponse<RestaurantResponseDto> toRestaurantResponseDtoPageResponse(Page<Restaurant> pageOfRestaurants);
}
