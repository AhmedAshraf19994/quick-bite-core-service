package com.quick_bite.core_service.product.mapper;


import com.quick_bite.core_service.product.dto.CreateProductDto;
import com.quick_bite.core_service.product.dto.ProductResponseDto;
import com.quick_bite.core_service.product.dto.UpdateBranchProductDetailsDto;
import com.quick_bite.core_service.product.dto.UpdateProductDto;
import com.quick_bite.core_service.product.entity.BranchProductDetails;
import com.quick_bite.core_service.product.entity.Category;
import com.quick_bite.core_service.product.entity.Product;
import com.quick_bite.core_service.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {

    @Mapping(target= "category", source = "category")
    @Mapping(target= "restaurant", source = "restaurant")
    @Mapping(target= "id", ignore = true)
    @Mapping(target= "name", source = "dto.name")
    @Mapping(target= "createdAt", ignore = true)
    @Mapping(target= "updatedAt", ignore = true)
    Product toProduct (CreateProductDto dto, Category category, Restaurant restaurant);

    @Mapping(target = "restaurantName", source = "restaurantName")
    @Mapping(target = "categoryName", source = "categoryName")
    ProductResponseDto toProductResponseDto(Product product, String restaurantName, String categoryName );

    void updateProduct(@MappingTarget Product product, UpdateProductDto dto);


    void updateBranchProductDetails(@MappingTarget BranchProductDetails branchProductDetails, UpdateBranchProductDetailsDto dto);
}
