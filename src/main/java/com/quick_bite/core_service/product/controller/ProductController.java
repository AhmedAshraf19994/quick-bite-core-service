package com.quick_bite.core_service.product.controller;

import com.quick_bite.core_service.common.PageResponse;
import com.quick_bite.core_service.common.Response;
import com.quick_bite.core_service.product.dto.*;
import com.quick_bite.core_service.product.entity.Category;
import com.quick_bite.core_service.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/restaurants/{restaurantId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<ProductResponseDto> createProduct (
            @PathVariable("restaurantId") Long restaurantId,
            @Valid @RequestBody CreateProductDto createProductDto
    ) {
        ProductResponseDto productResponseDto = this.productService.createProduct(restaurantId, createProductDto);

        return new Response<>(
                true,
                HttpStatus.CREATED.value(),
                "Product created successfully",
                productResponseDto
        );
    }

    @GetMapping("/products/{productId}")
    public Response<ProductResponseDto> getProductById (@PathVariable("productId") Long productId) {
        ProductResponseDto product = this.productService.getById(productId);
        return  new Response<>(
                true,
                HttpStatus.OK.value(),
                "Product found successfully",
                product
        );
    }

    @GetMapping("/branches/{branchId}/products")
    public Response<PageResponse<BranchProductResponseDto>> getAllProductsByBranch (
            @PathVariable("branchId") Long branchId,
            Pageable pageable
    ) {
        PageResponse<BranchProductResponseDto> pageOfProducts = this.productService.getAllProductsByBranch(branchId, pageable);

        return new Response<PageResponse<BranchProductResponseDto>>(
                true,
                HttpStatus.OK.value(),
                "Products retrieved successfully",
                pageOfProducts
        );
    }

    @GetMapping("/branches/{branchId}/products/{productId}")
    public Response<BranchProductResponseDto> getProductByBranchId(
            @PathVariable("branchId") Long branchId,
            @PathVariable("productId") Long productId
    ) {
        BranchProductResponseDto product = this.productService.getProductByBranchId(branchId, productId);

        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Product found successfully",
                product
        );
    }

    @GetMapping ("/restaurants/{restaurantId}/products")
    public Response<PageResponse<RestaurantProductResponseDto>> getAllProductsByRestaurantId (@PathVariable("restaurantId") Long restaurantId, Pageable pageable) {

        PageResponse<RestaurantProductResponseDto> pageOfProducts = this.productService.getAllByRestaurantsId (restaurantId, pageable);

        return  new Response<PageResponse<RestaurantProductResponseDto>>(
                true,
                HttpStatus.OK.value(),
                "Products retrieved successfully",
                pageOfProducts
        );
    }

    @GetMapping("/restaurants/{restaurantId}/categories")
    public Response<List<Category>> getAllCategoriesByRestaurantId (@PathVariable("restaurantId") Long restaurantId) {
        List<Category> categories = this.productService.findCategoriesByRestaurantId(restaurantId);

        return new Response<List<Category>>(
                true,
                HttpStatus.OK.value(),
                "Categories found successfully",
                categories
        );
    }

    @PatchMapping("/products/{productId}")
    public Response<ProductResponseDto> updateProduct (
            @PathVariable("productId")  Long productId,
            @RequestBody UpdateProductDto dto
    ) {
        ProductResponseDto product = this.productService.updateProduct(productId, dto);

        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Product updated successfully",
                product
        );
    }

    @PatchMapping("/branches/{branchId}/products/{productId}")
    public Response <BranchProductResponseDto> updateBranchProductDetails (
    @PathVariable("branchId") Long branchId,
    @PathVariable("productId") Long productId,
    @RequestBody  UpdateBranchProductDetailsDto dto
    ) {
      BranchProductResponseDto product = this.productService.updateBranchProductDetails(productId, branchId, dto);

      return new Response<>(
              true,
              HttpStatus.OK.value(),
              "Product updated successfully",
              product
      );

    }



}
