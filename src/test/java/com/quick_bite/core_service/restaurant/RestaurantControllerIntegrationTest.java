package com.quick_bite.core_service.restaurant;

import com.quick_bite.core_service.restaurant.dto.CreateRestaurantDto;
import com.quick_bite.core_service.restaurant.dto.UpdateRestaurantDto;
import com.quick_bite.core_service.restaurant.dto.UpdateRestaurantStatusDto;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserRepository;
import com.quick_bite.core_service.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"ADMIN"})
class RestaurantControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateRestaurantSuccess() throws Exception {
        // Create user
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        CreateRestaurantDto createRestaurantDto = new CreateRestaurantDto(
                "Tasty Bites",
                "https://example.com/logo.png",
                "Egypt"
        );

        this.mockMvc.perform(post(baseUrl + "/restaurants")
                .with(jwt().jwt(builder -> builder
                                .subject(savedUser.getId().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createRestaurantDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Restaurant created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value(createRestaurantDto.name()))
                .andExpect(jsonPath("$.data.primaryCountry").value(createRestaurantDto.primaryCountry()))
                .andExpect(jsonPath("$.data.logoUrl").value(createRestaurantDto.logoUrl()))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void shouldCreateRestaurantFailWithNameIsRequired() throws Exception {
        CreateRestaurantDto createRestaurantDto = new CreateRestaurantDto(
                null,
                "https://example.com/logo.png",
                "Egypt"
        );

        this.mockMvc.perform(post(baseUrl + "/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createRestaurantDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input provided check data for details"))
                .andExpect(jsonPath("$.data.name").value("Name is required"));
    }

    @Test
    void shouldCreateRestaurantFailWithPrimaryCountryIsRequired() throws Exception {
        CreateRestaurantDto createRestaurantDto = new CreateRestaurantDto(
                "Tasty Bites",
                "https://example.com/logo.png",
                null
        );

        this.mockMvc.perform(post(baseUrl + "/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createRestaurantDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input provided check data for details"))
                .andExpect(jsonPath("$.data.primaryCountry").value("Primary country is required"));
    }

    @Test
    void shouldUpdateRestaurantSuccess() throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .owner(savedUser)
                .name("Tasty Bites")
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egypt")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        UpdateRestaurantDto updateRestaurantDto = new UpdateRestaurantDto(
                "Updated Tasty Bites",
                "USA",
                "https://example.com/updated-logo.png"
        );

        this.mockMvc.perform(patch(baseUrl + "/restaurants/{restaurantId}", savedRestaurant.getId())
                .with(jwt().jwt(builder -> builder
                        .subject(savedUser.getId().toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateRestaurantDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurant updated successfully"))
                .andExpect(jsonPath("$.data.name").value(updateRestaurantDto.name()))
                .andExpect(jsonPath("$.data.primaryCountry").value(updateRestaurantDto.primaryCountry()))
                .andExpect(jsonPath("$.data.logoUrl").value(updateRestaurantDto.logoUrl()));
    }

    @Test
    void shouldUpdateRestaurantFailWithRestaurantNotFound() throws Exception {
        UpdateRestaurantDto updateRestaurantDto = new UpdateRestaurantDto(
                "Updated Tasty Bites",
                "USA",
                "https://example.com/updated-logo.png"
        );

        this.mockMvc.perform(patch(baseUrl + "/restaurants/{restaurantId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateRestaurantDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }

    @Test
    void shouldGetRestaurantByIdSuccess() throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .owner(savedUser)
                .name("Tasty Bites")
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egypt")
                .status(RestaurantStatus.ACTIVE)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        this.mockMvc.perform(get(baseUrl + "/restaurants/{restaurantId}", savedRestaurant.getId())
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurant retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(savedRestaurant.getId()))
                .andExpect(jsonPath("$.data.name").value(savedRestaurant.getName()));
    }

    @Test
    void shouldGetRestaurantByIdFailWithRestaurantNotFound() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/restaurants/{restaurantId}", 1)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }

    @Test
    void shouldGetAllActiveRestaurantsSuccess() throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Restaurant restaurant1 = Restaurant.builder()
                .owner(savedUser)
                .name("Tasty Bites")
                .logoUrl("https://example.com/logo1.png")
                .primaryCountry("Egypt")
                .status(RestaurantStatus.ACTIVE)
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .owner(savedUser)
                .name("Spicy Kitchen")
                .logoUrl("https://example.com/logo2.png")
                .primaryCountry("Egypt")
                .status(RestaurantStatus.ACTIVE)
                .build();

        Restaurant restaurant3 = Restaurant.builder()
                .owner(savedUser)
                .name("Suspended Place")
                .logoUrl("https://example.com/logo3.png")
                .primaryCountry("Egypt")
                .status(RestaurantStatus.SUSPENDED)
                .build();

        restaurantRepository.save(restaurant1);
        restaurantRepository.save(restaurant2);
        restaurantRepository.save(restaurant3);

        this.mockMvc.perform(get(baseUrl + "/restaurants")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurants retrieved successfully"))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldUpdateRestaurantStatusSuccess() throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        User admin = User.builder()
                .name("ahmed")
                .email("ahmedadmin@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User savedAdmin = userRepository.save(admin);

        Restaurant restaurant = Restaurant.builder()
                .owner(savedUser)
                .name("Tasty Bites")
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egypt")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        UpdateRestaurantStatusDto updateRestaurantStatusDto = new UpdateRestaurantStatusDto("active");

        this.mockMvc.perform(patch(baseUrl + "/restaurants/{restaurantId}/status", savedRestaurant.getId())
                        .with(jwt().jwt(builder -> builder.claim("authorities", UserRole.SYSTEM_ADMIN.getValue())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateRestaurantStatusDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurant status updated successfully"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void shouldUpdateRestaurantStatusFailWithForbiddenAction() throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        User admin = User.builder()
                .name("ahmed")
                .email("ahmedadmin@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.SYSTEM_ADMIN)
                .build();
        User savedAdmin = userRepository.save(admin);

        Restaurant restaurant = Restaurant.builder()
                .owner(savedUser)
                .name("Tasty Bites")
                .logoUrl("https://example.com/logo.png")
                .primaryCountry("Egypt")
                .status(RestaurantStatus.PENDING)
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        UpdateRestaurantStatusDto updateRestaurantStatusDto = new UpdateRestaurantStatusDto("active");

        this.mockMvc.perform(patch(baseUrl + "/restaurants/{restaurantId}/status", savedRestaurant.getId())
                        .with(jwt().jwt(builder -> builder.claim("authorities", UserRole.USER.getValue())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateRestaurantStatusDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("You are not allowed to perform this action"));
    }

    @Test
    void shouldUpdateRestaurantStatusFailWithRestaurantNotFound() throws Exception {
        UpdateRestaurantStatusDto updateRestaurantStatusDto = new UpdateRestaurantStatusDto("active");

        this.mockMvc.perform(patch(baseUrl + "/restaurants/{restaurantId}/status", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateRestaurantStatusDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }


}