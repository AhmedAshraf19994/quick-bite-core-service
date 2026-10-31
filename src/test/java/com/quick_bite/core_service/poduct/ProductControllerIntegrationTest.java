package com.quick_bite.core_service.poduct;

import com.quick_bite.core_service.branch.Branch;
import com.quick_bite.core_service.branch.BranchRepository;
import com.quick_bite.core_service.branch.CountryCode;
import com.quick_bite.core_service.branch.Currency;
import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.product.entity.BranchProductDetails;
import com.quick_bite.core_service.product.entity.Category;
import com.quick_bite.core_service.product.entity.Product;
import com.quick_bite.core_service.product.repository.BranchProductDetailsRepository;
import com.quick_bite.core_service.product.repository.CategoryRepository;
import com.quick_bite.core_service.product.repository.ProductRepository;
import com.quick_bite.core_service.restaurant.Restaurant;
import com.quick_bite.core_service.restaurant.RestaurantRepository;
import com.quick_bite.core_service.restaurant.RestaurantStatus;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserRepository;
import com.quick_bite.core_service.user.UserRole;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BranchProductDetailsRepository branchProductDetailsRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        branchProductDetailsRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        branchRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
    }

    // =========================
    // Test data helpers
    // =========================

    private User createUser(String email, UserRole role) {
        User user = User.builder()
                .name("Ahmed")
                .email(email)
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(role)
                .build();

        return userRepository.save(user);
    }

    private Restaurant createRestaurant(User owner) {
        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(owner)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.ACTIVE)
                .build();

        return restaurantRepository.save(restaurant);
    }

    private Branch createBranch(Restaurant restaurant, String label) {
        Branch branch = Branch.builder()
                .restaurant(restaurant)
                .countryCode(CountryCode.EGY)
                .label(label)
                .addressText("Test address")
                .isActive(true)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short) 10)
                .build();

        return branchRepository.save(branch);
    }

    private Category createCategory(Restaurant restaurant) {
        Category category = Category.builder()
                .name("Burgers")
                .restaurant(restaurant)
                .build();

        return categoryRepository.save(category);
    }

    private Product createProduct(Restaurant restaurant, Category category) {
        Product product = Product.builder()
                .name("Classic Burger")
                .description("Beef burger with cheese")
                .imageUrl("https://example.com/burger.png")
                .restaurant(restaurant)
                .category(category)
                .build();

        return productRepository.save(product);
    }

    private BranchProductDetails createBranchProductDetails(
            Branch branch,
            Product product
    ) {
        BranchProductDetails details = BranchProductDetails.builder()
                .branch(branch)
                .product(product)
                .price(100)
                .stock(20)
                .isAvailable(true)
                .build();

        return branchProductDetailsRepository.save(details);
    }

    private String createProductJson() {
        return """
                {
                    "name": "Classic Burger",
                    "description": "Beef burger with cheese",
                    "imageUrl": "https://example.com/burger.png",
                    "categoryName": "Burgers"
                }
                """;
    }

    // =========================
    // Create product
    // =========================

    @Test
    @DisplayName("Should create product and assign it to all restaurant branches")
    void shouldCreateProductSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);

        createBranch(restaurant, "Branch 1");
        createBranch(restaurant, "Branch 2");

        mockMvc.perform(post(baseUrl + "/restaurants/{restaurantId}/products",
                        restaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message")
                        .value("Product created successfully"))
                .andExpect(jsonPath("$.data.name").value("Classic Burger"));

        org.junit.jupiter.api.Assertions.assertEquals(
                1, productRepository.count()
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                2, branchProductDetailsRepository.count()
        );
    }

    // =========================
    // Get product by ID
    // =========================

    @Test
    @DisplayName("Should retrieve product by ID")
    void shouldGetProductByIdSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        Category category = createCategory(restaurant);
        Product product = createProduct(restaurant, category);

        mockMvc.perform(get(baseUrl + "/products/{productId}",
                        product.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Classic Burger"));
    }

    // =========================
    // Get product by branch
    // =========================

    @Test
    @DisplayName("Should retrieve product within branch to show it is details price, stock and if available")
    void shouldGetProductByBranchSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        Branch branch = createBranch(restaurant, "Branch 1");
        Category category = createCategory(restaurant);
        Product product = createProduct(restaurant, category);

        createBranchProductDetails(branch, product);

        mockMvc.perform(get(
                        baseUrl + "/branches/{branchId}/products/{productId}",
                        branch.getId(),
                        product.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.data.productName")
                        .value("Classic Burger"))
                .andExpect(jsonPath("$.data.productPrice").value(100))
                .andExpect(jsonPath("$.data.productStock").value(20))
                .andExpect(jsonPath("$.data.isAvailable").value(true));
    }

    // =========================
    // Get all products by branch
    // =========================

    @Test
    @DisplayName("Should retrieve paginated products by branch")
    void shouldGetAllProductsByBranchSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        Branch branch = createBranch(restaurant, "Branch 1");
        Category category = createCategory(restaurant);

        Product product = createProduct(restaurant, category);
        createBranchProductDetails(branch, product);

        mockMvc.perform(get(baseUrl + "/branches/{branchId}/products",
                        branch.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.isFirst").value(true))
                .andExpect(jsonPath("$.data.isLast").value(true));
    }

    // =========================
    // Get all products by restaurant
    // =========================

    @Test
    @DisplayName("Should retrieve paginated products by restaurant")
    void shouldGetAllProductsByRestaurantSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        Category category = createCategory(restaurant);

        createProduct(restaurant, category);

        mockMvc.perform(get(
                        baseUrl + "/restaurants/{restaurantId}/products",
                        restaurant.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].productName")
                        .value("Classic Burger"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    // =========================
    // Get categories by restaurant
    // =========================

    @Test
    @DisplayName("Should retrieve restaurant categories")
    void shouldGetCategoriesByRestaurantSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        createCategory(restaurant);

        mockMvc.perform(get(
                        baseUrl + "/restaurants/{restaurantId}/categories",
                        restaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Burgers"));
    }

    // =========================
    // Update product
    // =========================

    @Test
    @DisplayName("Should update product information")
    void shouldUpdateProductSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        Category category = createCategory(restaurant);
        Product product = createProduct(restaurant, category);

        String updateJson = """
                {
                    "name": "Updated Burger",
                    "description": "Updated description",
                    "imageUrl": "https://example.com/updated.png"
                }
                """;

        mockMvc.perform(patch(baseUrl + "/products/{productId}",
                        product.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Product updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Burger"))
                .andExpect(jsonPath("$.data.description").value("Updated description"));

        Product updatedProduct = productRepository.findById(product.getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                "Updated Burger", updatedProduct.getName()
        );
    }

    // =========================
    // Update branch product details
    // =========================

    @Test
    @DisplayName("Should update branch-specific product details")
    void shouldUpdateBranchProductDetailsSuccessfully() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        Branch branch = createBranch(restaurant, "Branch 1");
        Category category = createCategory(restaurant);
        Product product = createProduct(restaurant, category);

        createBranchProductDetails(branch, product);

        String updateJson = """
                {
                    "price": 150,
                    "stock": 30,
                    "isAvailable": true
                }
                """;

        mockMvc.perform(patch(
                        baseUrl + "/branches/{branchId}/products/{productId}",
                        branch.getId(),
                        product.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(owner.getId().toString())
                                .claim("authorities", "user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Product updated successfully"))
                .andExpect(jsonPath("$.data.productPrice").value(150))
                .andExpect(jsonPath("$.data.productStock").value(30))
                .andExpect(jsonPath("$.data.isAvailable").value(true));
    }

    @Test
    @DisplayName("Should update branch-specific product details as system admin")
    void shouldUpdateBranchProductDetailsSuccessfullyAsSystemAdmin() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        User admin = createUser("admin@test.com",  UserRole.SYSTEM_ADMIN);
        Restaurant restaurant = createRestaurant(owner);
        Branch branch = createBranch(restaurant, "Branch 1");
        Category category = createCategory(restaurant);
        Product product = createProduct(restaurant, category);

        createBranchProductDetails(branch, product);

        String updateJson = """
                {
                    "price": 150,
                    "stock": 30,
                    "isAvailable": true
                }
                """;

        mockMvc.perform(patch(
                        baseUrl + "/branches/{branchId}/products/{productId}",
                        branch.getId(),
                        product.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Product updated successfully"))
                .andExpect(jsonPath("$.data.productPrice").value(150))
                .andExpect(jsonPath("$.data.productStock").value(30))
                .andExpect(jsonPath("$.data.isAvailable").value(true));
    }

    @Test
    @DisplayName("Should update branch-specific product fail if an other user try it")
    void shouldUpdateBranchProductFailWithForbidden() throws Exception {

        User owner = createUser("owner@test.com", UserRole.USER);
        User regularUser = createUser("user@test.com", UserRole.USER);
        Restaurant restaurant = createRestaurant(owner);
        Branch branch = createBranch(restaurant, "Branch 1");
        Category category = createCategory(restaurant);
        Product product = createProduct(restaurant, category);

        createBranchProductDetails(branch, product);

        String updateJson = """
                {
                    "price": 150,
                    "stock": 30,
                    "isAvailable": true
                }
                """;

        mockMvc.perform(patch(
                        baseUrl + "/branches/{branchId}/products/{productId}",
                        branch.getId(),
                        product.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(regularUser.getId().toString())
                                .claim("authorities", "user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("You are not allowed to perform this action"));
    }
}
