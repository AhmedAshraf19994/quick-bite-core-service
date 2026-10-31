package com.quick_bite.core_service.product.service;

import com.quick_bite.core_service.branch.Branch;
import com.quick_bite.core_service.branch.BranchRepository;
import com.quick_bite.core_service.common.PageResponse;
import com.quick_bite.core_service.common.exception.ForbiddenActionException;
import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.product.dto.*;
import com.quick_bite.core_service.product.entity.BranchProductDetails;
import com.quick_bite.core_service.product.entity.Category;
import com.quick_bite.core_service.product.entity.Product;
import com.quick_bite.core_service.product.mapper.ProductMapper;
import com.quick_bite.core_service.product.repository.BranchProductDetailsRepository;
import com.quick_bite.core_service.product.repository.CategoryRepository;
import com.quick_bite.core_service.product.repository.ProductRepository;
import com.quick_bite.core_service.restaurant.Restaurant;
import com.quick_bite.core_service.restaurant.RestaurantRepository;
import com.quick_bite.core_service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final RestaurantRepository restaurantRepository;

    private final CurrentUserService  currentUserService;

    private final CategoryRepository categoryRepository;

    private final ProductMapper productMapper;

    private final BranchRepository branchRepository;

    private final BranchProductDetailsRepository  branchProductDetailsRepository;





    @Transactional
    public ProductResponseDto createProduct (Long restaurantId, CreateProductDto dto) {
        // get the restaurant
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        // validate that a restaurant owner or system admin can perform the action
        Long currentUserId = this.currentUserService.getCurrentUserId();
        Long restaurantOwnerId = restaurant.getOwnerId();
        boolean isResourceOwner = restaurantOwnerId.equals(currentUserId);
        boolean isAdmin = this.currentUserService.isAdmin();

        if(!isResourceOwner && !isAdmin) {
            throw new ForbiddenActionException();
        }

        // checking if category exists and if not create new category
        Category persistedCategory = this.categoryRepository.findByNameAndRestaurant(dto.categoryName(), restaurant)
                .orElseGet(() -> {
                    Category category = Category.builder()
                            .name(dto.categoryName())
                            .restaurant(restaurant)
                            .build();
                return this.categoryRepository.save(category);
                });

        // save the product
        Product product = this.productMapper.toProduct(dto, persistedCategory,restaurant);

        Product persistedProduct = this.productRepository.save(product);

        // populate the banch_product_details table with the new product
        // get all branches by restaurant
        List<Branch> branches = this.branchRepository.findByRestaurant(restaurant);

        // create the list of branchProductDetails entity
        List<BranchProductDetails> branchProductDetails = branches.stream()
                .map(branch ->  BranchProductDetails.builder()
                            .product(persistedProduct)
                            .branch(branch)
                            .price(0) // default value for price
                            .stock(0) // default value for stock
                            .isAvailable(false) // default value for is available
                            .build()
                ).toList();

            //write to branch_product_table
        this.branchProductDetailsRepository.saveAll(branchProductDetails);

        return this.productMapper.toProductResponseDto(persistedProduct, restaurant.getName(),persistedCategory.getName());

    }

    public BranchProductResponseDto getProductByBranchId(Long branchId, Long productId) {
        Branch branch = this.branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundException("Branch"));

        return this.branchProductDetailsRepository.findDetailedProductByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product"));
    }

    public PageResponse<BranchProductResponseDto> getAllProductsByBranch (Long branchId, Pageable pageable) {
        Branch branch = this.branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundException("Branch"));

        Page<BranchProductResponseDto> page = this.branchProductDetailsRepository.findByBranch(branchId, pageable);

        return new PageResponse<BranchProductResponseDto>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent(),
                page.isFirst(),
                page.isLast()
        );
    }

    public ProductResponseDto getById(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product"));


        return this.productMapper.toProductResponseDto(product,
                product.getRestaurant().getName(),
                product.getCategory().getName()
        );
    }

    public PageResponse<RestaurantProductResponseDto> getAllByRestaurantsId(Long restaurantId, Pageable pageable) {
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        Page<RestaurantProductResponseDto> pageOfProducts = this.productRepository.findAllProductsByRestaurant(restaurantId, pageable);

        return new PageResponse<RestaurantProductResponseDto>(
                pageOfProducts.getNumber(),
                pageOfProducts.getSize(),
                pageOfProducts.getTotalElements(),
                pageOfProducts.getTotalPages(),
                pageOfProducts.getContent(),
                pageOfProducts.isFirst(),
                pageOfProducts.isLast()
        );
    }

    public List<Category> findCategoriesByRestaurantId(Long restaurantId) {
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        return this.categoryRepository.findAllByRestaurantId(restaurantId);
    }

    public ProductResponseDto updateProduct(Long productId, UpdateProductDto dto) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product"));

        //to do verify user is authorized to do such action
        long restaurantOwnerId = product.getRestaurant().getOwnerId();
        long currentUserId = this.currentUserService.getCurrentUserId();
        boolean isAdmin =  this.currentUserService.isAdmin();

        if(restaurantOwnerId != currentUserId && !isAdmin){
            throw new ForbiddenActionException();
        }

        this.productMapper.updateProduct(product, dto);

        Product updatedProduct = this.productRepository.save(product);

        return this.productMapper.toProductResponseDto(updatedProduct, product.getCategory().getName(),product.getRestaurant().getName());
    }

    @Transactional
    public BranchProductResponseDto updateBranchProductDetails(Long productId, Long branchId, UpdateBranchProductDetailsDto dto) {
        BranchProductDetails branchProductDetails = this.branchProductDetailsRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product"));
        //to do verify user is authorized to do such action
        long restaurantOwner = branchProductDetails.getBranch().getRestaurant().getOwnerId();
        long currentUserId = this.currentUserService.getCurrentUserId();
        boolean isAdmin = this.currentUserService.isAdmin();
        if(restaurantOwner != currentUserId && !isAdmin){
            throw new ForbiddenActionException();
        }

        this.productMapper.updateBranchProductDetails(branchProductDetails, dto);

        BranchProductDetails savedBranchProductDetails = this.branchProductDetailsRepository.save(branchProductDetails);


        return new BranchProductResponseDto() {
            @Override
            public Long getProductId() {
                return branchProductDetails.getProduct().getId();
            }

            @Override
            public String getProductName() {
                return branchProductDetails.getProduct().getName();
            }

            @Override
            public String getProductDescription() {
                return branchProductDetails.getProduct().getDescription();
            }

            @Override
            public String getProductImageUrl() {
                return branchProductDetails.getProduct().getImageUrl();
            }

            @Override
            public int getProductPrice() {
                return savedBranchProductDetails.getPrice();
            }

            @Override
            public int getProductStock() {
                return savedBranchProductDetails.getStock();
            }

            @Override
            public boolean getIsAvailable() {
                return savedBranchProductDetails.isAvailable();
            }
        };

    }
}
