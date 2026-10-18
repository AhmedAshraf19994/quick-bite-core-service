package com.quick_bite.core_service.auth;

import com.quick_bite.core_service.auth.dto.LoginUserDto;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserRepository;
import com.quick_bite.core_service.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
     MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        this.userRepository.deleteAll();
    }

    @Test
    void shouldLoginSuccess() throws Exception {
        //given
        User user = User.builder()
                .email("test@gmail.com")
                .name("test")
                .phone("0123456789")
                .password(this.passwordEncoder.encode("Test@12345678"))
                .role(UserRole.USER)
                .build();

        this.userRepository.save(user);

        LoginUserDto dto = new LoginUserDto(
                "test@gmail.com",
                "Test@12345678"
        );

        //when then
        MvcResult result = this.mockMvc.perform(post(baseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("refreshToken"));
    }

    @Test
    void shouldLoginFailWithInvalidInput()throws Exception {
        //given
        LoginUserDto dto = new LoginUserDto(
                null,
                null );

        this.mockMvc.perform(post(baseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.email").value("Email is required"))
                .andExpect(jsonPath("$.data.password").value("Password is required"));
    }

    @Test
    void shouldLoginFailWithNotFoundUser () throws Exception {
        //given
        LoginUserDto dto = new LoginUserDto(
                "test@test.com",
                "Test@12345"
        );
        //when then
        this.mockMvc.perform(post(baseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    void shouldLoginFailWithWrongPassword () throws Exception {
        //given
        User user = User.builder()
                .email("test@gmail.com")
                .name("test")
                .phone("0123456789")
                .password(this.passwordEncoder.encode("Test@12345678"))
                .role(UserRole.USER)
                .build();

        LoginUserDto dto = new LoginUserDto(
                "test@gmail.com",
                "Test@12345677"
        );

        this.userRepository.save(user);

        //when then
        this.mockMvc.perform(post(baseUrl + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Bad credentials"));

    }


}