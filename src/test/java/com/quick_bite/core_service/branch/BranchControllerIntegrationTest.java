package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.branch.dto.CreateBranchDto;
import com.quick_bite.core_service.branch.dto.UpdateBranchDto;
import com.quick_bite.core_service.branch.dto.UpdateBranchStatusDto;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BranchControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setup () {
        this.branchRepository.deleteAll();
        this.restaurantRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @AfterEach
    void tearDown () {
        this.branchRepository.deleteAll();
        this.restaurantRepository.deleteAll();
        this.userRepository.deleteAll();
    }


    @Test
    @DisplayName("should create branch success with resource owner role")
    void shouldCreateBranchSuccessWithResourceOwnerRole () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(savedUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        CreateBranchDto createBranchDto = new CreateBranchDto(
                "egy",
                "Test Branch",
                "test address",
                BigDecimal.valueOf(30.123456),
                BigDecimal.valueOf(31.123456),
                (short) 10,
                LocalTime.of(8,0),
                LocalTime.of(22,0),
                true,
                "egp",
                (short) 10
        );

        //when then
        this.mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/restaurants/{restaurantId}/branches", savedRestaurant.getId())
                .with(jwt().jwt(builder -> builder
                        .subject(savedUser.getId().toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(createBranchDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Branch created successfully"))
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    @DisplayName("should create branch success with admin role")
    void shouldCreateBranchSuccessWithAdminRole () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User currentUser = userRepository.save(user);

        User restaurantOwner = User.builder()
                .name("owner")
                .email("owner@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Owner@12345"))
                .role(UserRole.USER)
                .build();
        User savedRestaurantOwner = userRepository.save(restaurantOwner);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(restaurantOwner)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        CreateBranchDto createBranchDto = new CreateBranchDto(
                "egy",
                "Test Branch",
                "test address",
                BigDecimal.valueOf(30.123456),
                BigDecimal.valueOf(31.123456),
                (short) 10,
                LocalTime.of(8,0),
                LocalTime.of(22,0),
                true,
                "egp",
                (short) 10
        );

        //when then
        this.mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/restaurants/{restaurantId}/branches", savedRestaurant.getId())
                .with(jwt().jwt(builder -> builder
                        .subject(currentUser.getId().toString())
                        .claim("authorities", "system_admin")
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(createBranchDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Branch created successfully"))
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    @DisplayName("should return 403 forbidden when user is not the owner of the restaurant")
    void shouldCreateBranchFailWithForbiddenActionException () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User currentUser = userRepository.save(user);

        User restaurantOwner = User.builder()
                .name("owner")
                .email("owner@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Owner@12345"))
                .role(UserRole.USER)
                .build();
        User savedRestaurantOwner = userRepository.save(restaurantOwner);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(restaurantOwner)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        CreateBranchDto createBranchDto = new CreateBranchDto(
                "egy",
                "Test Branch",
                "test address",
                BigDecimal.valueOf(30.123456),
                BigDecimal.valueOf(31.123456),
                (short) 10,
                LocalTime.of(8,0),
                LocalTime.of(22,0),
                true,
                "egp",
                (short) 10
        );

        //when then
        this.mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/restaurants/{restaurantId}/branches", savedRestaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(currentUser.getId().toString())
                                .claim("authorities", "user")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createBranchDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("You are not allowed to perform this action"));
    }

    @Test
    @DisplayName("should return 404 not found when restaurant does not exist")
    void shouldCreateBranchFailWithNoRestaurantFound () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User currentUser = userRepository.save(user);

        User restaurantOwner = User.builder()
                .name("owner")
                .email("owner@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Owner@12345"))
                .role(UserRole.USER)
                .build();
        User savedRestaurantOwner = userRepository.save(restaurantOwner);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(restaurantOwner)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        CreateBranchDto createBranchDto = new CreateBranchDto(
                "egy",
                "Test Branch",
                "test address",
                BigDecimal.valueOf(30.123456),
                BigDecimal.valueOf(31.123456),
                (short) 10,
                LocalTime.of(8,0),
                LocalTime.of(22,0),
                true,
                "egp",
                (short) 10
        );

        //when then
        this.mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/restaurants/{restaurantId}/branches", savedRestaurant.getId() + 1)
                        .with(jwt().jwt(builder -> builder
                                .subject(currentUser.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createBranchDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }

    @Test
    @DisplayName("should get all branches by restaurant success")
    void shouldGetAllBranchesByRestaurantSuccess () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(savedUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch1 = Branch.builder()
                .restaurant(restaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(true)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();Branch branch2 = Branch.builder()
                .restaurant(restaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(true)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        this.branchRepository.save(branch1);
        this.branchRepository.save(branch2);

        //when then
        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/restaurants/{restaurantId}/branches", savedRestaurant.getId())
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Branches fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("should return 404 not found when restaurant does not exist")
    void shouldGetAllBranchesByRestaurantFailWithNoRestaurantFound () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User savedUser = userRepository.save(user);

        //when then
        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/restaurants/{restaurantId}/branches", 99999)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }

    @Test
    @DisplayName("should update branch success without updating status with role user")
    void shouldUpdateBranchSuccessWithUserRole () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User currentUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(currentUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(false)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        Branch savedBranch = branchRepository.save(branch);

        UpdateBranchDto updateBranchDto = new UpdateBranchDto(
                "updated label",
                "Updated Branch",
                BigDecimal.valueOf(30.654321),
                BigDecimal.valueOf(31.654321),
                (short) 15,
                LocalTime.of(10,0),
                LocalTime.of(21,0),
                false,
                "egp",
                (short) 15
        );

        //when then
        this.mockMvc.perform(patch(baseUrl + "/branches/{branchId}", savedBranch.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(currentUser.getId().toString())
                                .claim("authorities", "user")
                        ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBranchDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Branch updated successfully"))
                .andExpect(jsonPath("$.data.label").value("updated label"))
                .andExpect(jsonPath("$.data.addressText").value("Updated Branch"))
                .andExpect(jsonPath("$.data.lat").value(30.654321))
                .andExpect(jsonPath("$.data.lng").value(31.654321))
                .andExpect(jsonPath("$.data.deliveryRadius").value(15))
                .andExpect(jsonPath("$.data.acceptOrders").value(false))
                .andExpect(jsonPath("$.data.currency").value("EGP"))
                .andExpect(jsonPath("$.data.isActive").value(false))

                .andExpect(jsonPath("$.data.commission").value(15));

    }

    @Test
    @DisplayName("should update branch success without updating status with admin role")
    void shouldUpdateBranchSuccessWithAdminRole () throws Exception {
        //given
        User user1 = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User admin = userRepository.save(user1);

        User user2 = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User currentUser = userRepository.save(admin);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(currentUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(false)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        Branch savedBranch = branchRepository.save(branch);

        UpdateBranchDto updateBranchDto = new UpdateBranchDto(
                "updated label",
                "Updated Branch",
                BigDecimal.valueOf(30.654321),
                BigDecimal.valueOf(31.654321),
                (short) 15,
                LocalTime.of(10,0),
                LocalTime.of(21,0),
                false,
                "egp",
                (short) 15
        );

        //when then
        this.mockMvc.perform(patch(baseUrl + "/branches/{branchId}", savedBranch.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBranchDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Branch updated successfully"))
                .andExpect(jsonPath("$.data.label").value("updated label"))
                .andExpect(jsonPath("$.data.addressText").value("Updated Branch"))
                .andExpect(jsonPath("$.data.lat").value(30.654321))
                .andExpect(jsonPath("$.data.lng").value(31.654321))
                .andExpect(jsonPath("$.data.deliveryRadius").value(15))
                .andExpect(jsonPath("$.data.acceptOrders").value(false))
                .andExpect(jsonPath("$.data.currency").value("EGP"))
                .andExpect(jsonPath("$.data.isActive").value(false))
                .andExpect(jsonPath("$.data.commission").value(15));

    }

    @Test
    @DisplayName("should update branch fail with 404 not found branch ")
    void shouldUpdateBranchFailWithNotFoundBranch () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User currentUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(currentUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(true)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        Branch savedBranch = branchRepository.save(branch);

        UpdateBranchDto updateBranchDto = new UpdateBranchDto(
                "updated label",
                "Updated Branch",
                BigDecimal.valueOf(30.654321),
                BigDecimal.valueOf(31.654321),
                (short) 15,
                LocalTime.of(10,0),
                LocalTime.of(21,0),
                false,
                "egp",
                (short) 15
        );

        //when then
        this.mockMvc.perform(patch(baseUrl + "/branches/{branchId}", savedBranch.getId() + 1)
                        .with(jwt().jwt(builder -> builder
                                .subject(currentUser.getId().toString())
                                .claim("authorities", "user")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBranchDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Branch not found"));
    }

    @Test
    @DisplayName("should update branch fail with 404 forbidden action you are not allowed to perform this action ")
    void shouldUpdateBranchFailWithForbiddenActionException () throws Exception {
        //given
        User user1 = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User currentUser = userRepository.save(user1);

        User user2 = User.builder()
                .name("user2")
                .email("user2@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();

        User restaurantOwner = userRepository.save(user2);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(restaurantOwner)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(true)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        Branch savedBranch = branchRepository.save(branch);

        UpdateBranchDto updateBranchDto = new UpdateBranchDto(
                "updated label",
                "Updated Branch",
                BigDecimal.valueOf(30.654321),
                BigDecimal.valueOf(31.654321),
                (short) 15,
                LocalTime.of(10,0),
                LocalTime.of(21,0),
                false,
                "egp",
                (short) 15
        );

        //when then
        this.mockMvc.perform(patch(baseUrl + "/branches/{branchId}", savedBranch.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(currentUser.getId().toString())
                                .claim("authorities", "user")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBranchDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("You are not allowed to perform this action"));


    }

    @Test
    @DisplayName("should update branch success with role admin")
    void shouldUpdateBranchStatusSuccess () throws Exception
    {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User admin = userRepository.save(user);

        User user2 = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User restaurantOwner = userRepository.save(admin);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(restaurantOwner)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(false)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        Branch savedBranch = branchRepository.save(branch);

        UpdateBranchStatusDto dto = new UpdateBranchStatusDto(true);

        //when then
        this.mockMvc.perform(patch(baseUrl + "/branches/{branchId}/status", savedBranch.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Branch status updated successfully"))
                .andExpect(jsonPath("$.data.isActive").value(true));

    }

    @Test
    @DisplayName("should update branch status fail with role user ")
    void shouldUpdateBranchStatusFailWith404 () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User admin = userRepository.save(user);

        User user2 = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User restaurantOwner = userRepository.save(admin);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(restaurantOwner)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(false)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        Branch savedBranch = branchRepository.save(branch);

        UpdateBranchStatusDto dto = new UpdateBranchStatusDto(true);

        //when then
        this.mockMvc.perform(patch(baseUrl + "/branches/{branchId}/status", savedBranch.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(restaurantOwner.getId().toString())
                                .claim("authorities", "user")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("You are not allowed to perform this action"));
    }

    @Test
    @DisplayName("should get nearby branches success")
    void shouldGetNearbyBranchesSuccess () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(savedUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.ACTIVE)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch1 = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(true)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        Branch branch2 = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Maadi Branch")
                .addressText("Maadi, Cairo, Egypt")
                .isActive(true)
                .lat(new BigDecimal("30.0100"))
                .lng(new BigDecimal("31.2500"))
                .deliveryRadius((short) 10)
                .opensAt(LocalTime.of(10, 0))
                .closesAt(LocalTime.of(22, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)12)
                .build();

        this.branchRepository.save(branch1);
        this.branchRepository.save(branch2);

        //when then - search near Nasr City
        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/branches/nearby")
                .param("lat", "30.0626")
                .param("lng", "31.2497")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Nearby branches fetched successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("should return empty list when no nearby branches found")
    void shouldGetNearbyBranchesReturnEmptyList () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(savedUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.ACTIVE)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(true)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 1)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        this.branchRepository.save(branch);

        //when then - search far away from the branch
        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/branches/nearby")
                .param("lat", "31.2000")
                .param("lng", "32.2000")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Nearby branches fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("should return empty list when branch is inactive")
    void shouldGetNearbyBranchesReturnEmptyListWhenBranchInactive () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(this.passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .owner(savedUser)
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egy")
                .status(RestaurantStatus.ACTIVE)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Branch branch = Branch.builder()
                .restaurant(savedRestaurant)
                .countryCode(CountryCode.EGY)
                .label("Nasr City Branch")
                .addressText("Nasr City, Cairo, Egypt")
                .isActive(false)
                .lat(new BigDecimal("30.0626"))
                .lng(new BigDecimal("31.2497"))
                .deliveryRadius((short) 5)
                .opensAt(LocalTime.of(9, 0))
                .closesAt(LocalTime.of(23, 0))
                .acceptOrders(true)
                .currency(Currency.EGP)
                .commission((short)10)
                .build();

        this.branchRepository.save(branch);

        //when then - search near the inactive branch
        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/branches/nearby")
                .param("lat", "30.0626")
                .param("lng", "31.2497")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Nearby branches fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}