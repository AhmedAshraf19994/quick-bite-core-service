
package com.quick_bite.core_service.rbac;

import com.quick_bite.core_service.branch.*;
import com.quick_bite.core_service.rbac.dto.CreateRestaurantMemberDto;
import com.quick_bite.core_service.rbac.dto.RestaurantMemberInfo;
import com.quick_bite.core_service.rbac.dto.UpdateMemberBranchesDto;
import com.quick_bite.core_service.rbac.dto.UpdateRestaurantMemberDto;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantMemberControllerIntegrationTest {

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

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    private User admin;
    private User memberUser;
    private Restaurant restaurant;
    private Branch branch1;
    private Branch branch2;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM member_branches");
        jdbcTemplate.update("DELETE FROM restaurant_members");

        branchRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();

        admin = createUser("admin@test.com", UserRole.SYSTEM_ADMIN);
        memberUser = createUser("member@test.com", UserRole.USER);

        restaurant = createRestaurant(admin);

        branch1 = createBranch(restaurant, "Nasr City Branch");
        branch2 = createBranch(restaurant, "Maadi Branch");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM member_branches");
        jdbcTemplate.update("DELETE FROM restaurant_members");

        branchRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
    }

    // =====================================================
    // CREATE MEMBER
    // =====================================================

    @Test
    @DisplayName("should create restaurant member successfully as system admin")
    void shouldCreateRestaurantMemberSuccessfullyWithSystemAdmin() throws Exception {

        // given
        CreateRestaurantMemberDto dto = new CreateRestaurantMemberDto(
                memberUser.getId(),
                Set.of(branch1.getId()),
                "staff"
        );

        // when then
        mockMvc.perform(post(baseUrl + "/restaurants/{restaurantId}/members",
                        restaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Member created successfully"))
                .andExpect(jsonPath("$.data.userId").value(memberUser.getId()))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurant.getId()))
                .andExpect(jsonPath("$.data.restaurantRole").value("staff"))
                .andExpect(jsonPath("$.data.branchIds[0]").value(branch1.getId()));

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM restaurant_members WHERE restaurant_id = ? AND user_id = ?",
                Integer.class,
                restaurant.getId(),
                memberUser.getId()
        ));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member_branches WHERE branch_id = ? ",
                Integer.class,
                branch1.getId()

        ));
    }

    @Test
    @DisplayName("should create restaurant success with owner role")
    void shouldCreateRestaurantMemberSuccessWithOwner() throws Exception {

        // given
        CreateRestaurantMemberDto dto = new CreateRestaurantMemberDto(
                memberUser.getId(),
                Set.of(branch1.getId()),
                "staff"
        );

        RestaurantMemberInfo memberInfo = new RestaurantMemberInfo(
                restaurant.getId(),
                "owner",
                Set.of(branch1.getId(), branch2.getId())
        );

        // when then
        mockMvc.perform(post(baseUrl + "/restaurants/{restaurantId}/members",
                        restaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "user")
                                .claim("restaurantMemberInfo", memberInfo)
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Member created successfully"))
                .andExpect(jsonPath("$.data.userId").value(memberUser.getId()))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurant.getId()))
                .andExpect(jsonPath("$.data.restaurantRole").value("staff"))
                .andExpect(jsonPath("$.data.branchIds[0]").value(branch1.getId()));

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM restaurant_members WHERE restaurant_id = ? AND user_id = ?",
                Integer.class,
                restaurant.getId(),
                memberUser.getId()
        ));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member_branches WHERE branch_id = ? ",
                Integer.class,
                branch1.getId()

        ));
    }
    @Test
    @DisplayName("should create restaurant member fail with forbidden ")
    void shouldCreateRestaurantMemberFailWithForbiddenException() throws Exception {

        // given
        CreateRestaurantMemberDto dto = new CreateRestaurantMemberDto(
                memberUser.getId(),
                Set.of(branch1.getId()),
                "staff"
        );

        // when then
        mockMvc.perform(post(baseUrl + "/restaurants/{restaurantId}/members",
                        restaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "user")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 400 when creating member with invalid data")
    void shouldCreateRestaurantMemberFailWithInvalidData() throws Exception {

        // given
        CreateRestaurantMemberDto dto = new CreateRestaurantMemberDto(
                null,
                Set.of(),
                ""
        );

        // when then
        mockMvc.perform(post(baseUrl + "/restaurants/{restaurantId}/members",
                        restaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 404 when creating member for nonexistent restaurant")
    void shouldCreateRestaurantMemberFailWithRestaurantNotFound() throws Exception {

        // given
        CreateRestaurantMemberDto dto = new CreateRestaurantMemberDto(
                memberUser.getId(),
                Set.of(branch1.getId()),
                "staff"
        );

        // when then
        mockMvc.perform(post(baseUrl + "/restaurants/{restaurantId}/members",
                        99999L)
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404));
    }

    // =====================================================
    // GET MEMBERS
    // =====================================================

    @Test
    @DisplayName("should get all restaurant members successfully")
    void shouldGetAllRestaurantMembersSuccessfully() throws Exception {

        // given
        Long memberId = createRestaurantMember(
                restaurant.getId(),
                memberUser.getId(),
                "staff"
        );

        assignBranch(memberId, branch1.getId());

        // when then
        mockMvc.perform(get(baseUrl + "/restaurants/{restaurantId}/members",
                        restaurant.getId())
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("members fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].userId").value(memberUser.getId()))
                .andExpect(jsonPath("$.data[0].restaurantRole").value("staff"));
    }

    @Test
    @DisplayName("should return 404 when getting members for nonexistent restaurant")
    void shouldGetAllRestaurantMembersFailWithRestaurantNotFound() throws Exception {

        mockMvc.perform(get(baseUrl + "/restaurants/{restaurantId}/members",
                        99999L)
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }

    // =====================================================
    // UPDATE MEMBER ROLE
    // =====================================================

    @Test
    @DisplayName("should update restaurant member role successfully")
    void shouldUpdateRestaurantMemberRoleSuccessfully() throws Exception {

        // given
        Long memberId = createRestaurantMember(
                restaurant.getId(),
                memberUser.getId(),
                "staff"
        );

        UpdateRestaurantMemberDto dto =
                new UpdateRestaurantMemberDto("branch_manager");

        // when then
        mockMvc.perform(patch(
                        baseUrl + "/restaurants/{restaurantId}/members/{memberId}",
                        restaurant.getId(),
                        memberId)
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Member updated successfully"))
                .andExpect(jsonPath("$.data.restaurantRole").value("branch_manager"));

        assertEquals("branch_manager", jdbcTemplate.queryForObject("""
                SELECT r.name
                FROM restaurant_members rm
                JOIN roles r ON r.id = rm.role_id
                WHERE rm.id = ?
                """, String.class, memberId));
    }

    @Test
    @DisplayName("should return 404 when updating nonexistent member")
    void shouldUpdateRestaurantMemberFailWithMemberNotFound() throws Exception {

        // given
        UpdateRestaurantMemberDto dto =
                new UpdateRestaurantMemberDto("branch_manager");

        // when then
        mockMvc.perform(patch(
                        baseUrl + "/restaurants/{restaurantId}/members/{memberId}",
                        restaurant.getId(),
                        99999L)
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound());
    }

    // =====================================================
    // DELETE MEMBER
    // =====================================================

    @Test
    @DisplayName("should return 404 when deleting nonexistent member")
    void shouldDeleteRestaurantMemberFailWithMemberNotFound() throws Exception {

        mockMvc.perform(delete(
                        baseUrl + "/restaurants/{restaurantId}/members/{memberId}",
                        restaurant.getId(),
                        99999L)
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                )
                .andExpect(status().isNotFound());
    }

    // =====================================================
    // REPLACE MEMBER BRANCHES
    // =====================================================

    @Test
    @DisplayName("should replace member branches successfully")
    void shouldReplaceMemberBranchesSuccessfully() throws Exception {

        // given
        Long memberId = createRestaurantMember(
                restaurant.getId(),
                memberUser.getId(),
                "staff"
        );

        assignBranch(memberId, branch1.getId());

        UpdateMemberBranchesDto dto =
                new UpdateMemberBranchesDto(Set.of(branch2.getId()));

        // when then
        mockMvc.perform(put(
                        baseUrl + "/restaurants/{restaurantId}/members/{memberId}/branches",
                        restaurant.getId(),
                        memberId)
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.message").value("Member branches updated successfully"));

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member_branches WHERE member_id = ?",
                Integer.class,
                memberId
        ));

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member_branches WHERE member_id = ? AND branch_id = ?",
                Integer.class,
                memberId,
                branch2.getId()
        ));

        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member_branches WHERE member_id = ? AND branch_id = ?",
                Integer.class,
                memberId,
                branch1.getId()
        ));
    }

    @Test
    @DisplayName("should return 400 when replacing branches with empty branch IDs")
    void shouldReplaceMemberBranchesFailWithEmptyBranchIds() throws Exception {

        // given
        Long memberId = createRestaurantMember(
                restaurant.getId(),
                memberUser.getId(),
                "staff"
        );

        UpdateMemberBranchesDto dto =
                new UpdateMemberBranchesDto(Set.of());

        // when then
        mockMvc.perform(put(
                        baseUrl + "/restaurants/{restaurantId}/members/{memberId}/branches",
                        restaurant.getId(),
                        memberId)
                        .with(jwt().jwt(builder -> builder
                                .subject(admin.getId().toString())
                                .claim("authorities", "system_admin")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    // =====================================================
    // TEST DATA HELPERS
    // =====================================================

    private User createUser(String email, UserRole role) {
        User user = User.builder()
                .name("Test User")
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
                .status(RestaurantStatus.PENDING)
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

    private Long createRestaurantMember(
            Long restaurantId,
            Long userId,
            String roleName
    ) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO restaurant_members (user_id, restaurant_id, role_id)
                SELECT ?, ?, id
                FROM roles
                WHERE name = ?
                RETURNING id
                """,
                Long.class,
                userId,
                restaurantId,
                roleName
        );
    }

    private void assignBranch(Long memberId, Long branchId) {
        jdbcTemplate.update("""
                INSERT INTO member_branches (member_id, branch_id)
                VALUES (?, ?)
                """,
                memberId,
                branchId
        );
    }
}
