package com.quick_bite.core_service.user;

import com.quick_bite.core_service.user.dto.CreateUserDto;
import com.quick_bite.core_service.user.dto.UpdateUserDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUserSuccess() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
                "email@email.com",
                "name",
                "0123987654",
                "A@hmed123456",
                "user"
        );

        this.mockMvc.perform(post(baseUrl+ "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(createUserDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value(createUserDto.email()))
                .andExpect(jsonPath("$.data.name").value(createUserDto.name()))
                .andExpect(jsonPath("$.data.phone").value(createUserDto.phone()));
    }

    @Test
    void shouldCreateUserFailWithUserExistsException() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
                "email@email.com",
                "name",
                "0123987654",
                "A@hmed123456",
                "user"
        );
        this.mockMvc.perform(post(baseUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(createUserDto))
                .accept(MediaType.APPLICATION_JSON)
        );

        this.mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(createUserDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("User already exists with the same email"));

    }

    @Test
    void shouldCreateUserFailWithRoleIsRequired() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
                "email@email.com",
                "name",
                "0123987654",
                "A@hmed123456",
              null
        );

        this.mockMvc.perform(post( baseUrl + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createUserDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input provided check data for details"))
                .andExpect(jsonPath("$.data.role").value("Role is required"));

    }

    @Test
    void shouldCreateUserFailWithRoleNotValid() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
                "email@email.com",
                "name",
                "0123987654",
                "A@hmed123456",
              "member"
        );

        this.mockMvc.perform(post( baseUrl + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createUserDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input provided check data for details"))
                .andExpect(jsonPath("$.data.role").value("Invalid role"));
    }

    @Test
    void shouldUpdateUserSuccess () throws Exception {
        //given
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password("Ahmed@12345")
                .role(UserRole.USER)
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto(
                "updated name",
                "0123456789"
        );

        User savedUser = this.userRepository.save(user);

        //when then
        this.mockMvc.perform(patch(baseUrl + "/users/{userId}", savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateUserDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.name").value("updated name"))
                .andExpect(jsonPath("$.data.phone").value("0123456789"));

    }

    @Test
    void shouldUpdateUserFailWithUserNotFound () throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto(
                "updated name",
                "0123456789"
        );
        this.mockMvc.perform(patch(baseUrl + "/users/{userId}", 1) // id that doesn't exist
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldGetUserByIdSuccess () throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password("Ahmed@12345")
                .role(UserRole.USER)
                .build();
       User savedUser = this.userRepository.save(user);

        this.mockMvc.perform(get(baseUrl + "/users/{userId}",savedUser.getId())
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User find success"))
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()));
    }

    @Test
    void shouldGetUserByIdFailWithNoUserFound () throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password("Ahmed@12345")
                .role(UserRole.USER)
                .build();
       User savedUser = this.userRepository.save(user);

        this.mockMvc.perform(get(baseUrl + "/users/{userId}",1)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldDeleteUserSuccess () throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password("Ahmed@12345")
                .role(UserRole.USER)
                .build();

        User savedUser = this.userRepository.save(user);

        this.mockMvc.perform(delete(baseUrl + "/users/{userId}",savedUser.getId() )
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
    @Test
    void shouldDeleteUserFailWithNoUserFound () throws Exception {
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password("Ahmed@12345")
                .role(UserRole.USER)
                .build();

        User savedUser = this.userRepository.save(user);

        this.mockMvc.perform(delete(baseUrl + "/users/{userId}",savedUser.getId() + 1 )
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldGetAllUsersSuccess () throws Exception {
        User user1 = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password("Ahmed@12345")
                .role(UserRole.USER)
                .build();

        User user2 = User.builder()
                .name("reham")
                .email("reham@test.com")
                .phone("123456789")
                .password("reham@12345")
                .role(UserRole.USER)
                .build();

        this.userRepository.save(user1);
        this.userRepository.save(user2);

        this.mockMvc.perform(get(baseUrl + "/users")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Users find success"))
                .andExpect(jsonPath("$.data", hasSize(2)));

    }
}