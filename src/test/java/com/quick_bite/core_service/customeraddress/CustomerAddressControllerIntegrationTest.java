package com.quick_bite.core_service.customeraddress;

import com.quick_bite.core_service.customeraddress.dto.CreateCustomerAddressDto;
import com.quick_bite.core_service.customeraddress.dto.UpdateCustomerAddressDto;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"ADMIN"})
class CustomerAddressControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        customerAddressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateCustomerAddressSuccess() throws Exception {
        // Create a user first
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        CreateCustomerAddressDto createCustomerAddressDto = new CreateCustomerAddressDto(
                "Home",
                "Egypt",
                "Cairo",
                "Nasr City",
                "Building A",
                "Apartment 5",
                "home",
                new BigDecimal("31.2357"),
                new BigDecimal("30.0444")
        );

        this.mockMvc.perform(post(baseUrl + "/customer-addresses")
                        .with(jwt().jwt(jwt -> jwt
                                .subject(savedUser.getId().toString())
                        ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(createCustomerAddressDto))
                .accept(MediaType.APPLICATION_JSON)

        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Address created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.label").value(createCustomerAddressDto.label()))
                .andExpect(jsonPath("$.data.country").value(createCustomerAddressDto.country()))
                .andExpect(jsonPath("$.data.city").value(createCustomerAddressDto.city()));
    }

    @Test
    void shouldCreateCustomerAddressFailWithLabelIsRequired() throws Exception {
        CreateCustomerAddressDto createCustomerAddressDto = new CreateCustomerAddressDto(
                null,
                "Egypt",
                "Cairo",
                "Nasr City",
                "Building A",
                "Apartment 5",
                "home",
                new BigDecimal("31.2357"),
                new BigDecimal("30.0444")
        );

        this.mockMvc.perform(post(baseUrl + "/customer-addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createCustomerAddressDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input provided check data for details"))
                .andExpect(jsonPath("$.data.label").value("Label is required"));
    }

    @Test
    void shouldCreateCustomerAddressFailWithInvalidAddressType() throws Exception {
        CreateCustomerAddressDto createCustomerAddressDto = new CreateCustomerAddressDto(
                "Home",
                "Egypt",
                "Cairo",
                "Nasr City",
                "Building A",
                "Apartment 5",
                "invalid_type",
                new BigDecimal("31.2357"),
                new BigDecimal("30.0444")
        );

        this.mockMvc.perform(post(baseUrl + "/customer-addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createCustomerAddressDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input provided check data for details"))
                .andExpect(jsonPath("$.data.type").value("Invalid address type. Allowed values are: HOME, WORK, OTHER"));
    }

    @Test
    void shouldUpdateCustomerAddressSuccess() throws Exception {
        // Create user
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        // Create customer address
        CustomerAddress customerAddress = CustomerAddress.builder()
                .user(savedUser)
                .label("Home")
                .country("Egypt")
                .city("Cairo")
                .street("Nasr City")
                .building("Building A")
                .apartmentNumber("Apartment 5")
                .type(AddressType.HOME)
                .isDefault(false)
                .longitude(new BigDecimal("31.2357"))
                .latitude(new BigDecimal("30.0444"))
                .build();
        CustomerAddress savedAddress = customerAddressRepository.save(customerAddress);

        UpdateCustomerAddressDto updateCustomerAddressDto = new UpdateCustomerAddressDto(
                "Work",
                "Egypt",
                "Alexandria",
                "Street 1",
                "Building B",
                "Apartment 10",
                "work",
                new BigDecimal("31.2000"),
                new BigDecimal("30.0000")
        );

        this.mockMvc.perform(patch(baseUrl + "/customer-addresses/{addressId}", savedAddress.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateCustomerAddressDto))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Customer address updated successfully"))
                .andExpect(jsonPath("$.data.label").value("Work"))
                .andExpect(jsonPath("$.data.city").value("Alexandria"));
    }

    @Test
    void shouldUpdateCustomerAddressFailWithAddressNotFound() throws Exception {
        UpdateCustomerAddressDto updateCustomerAddressDto = new UpdateCustomerAddressDto(
                "Work",
                "Egypt",
                "Alexandria",
                "Street 1",
                "Building B",
                "Apartment 10",
                "work",
                new BigDecimal("31.2000"),
                new BigDecimal("30.0000")
        );

        this.mockMvc.perform(patch(baseUrl + "/customer-addresses/{addressId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateCustomerAddressDto))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("CustomerAddress not found"));
    }

    @Test
    void shouldGetCustomerAddressByIdSuccess() throws Exception {
        // Create user
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        // Create customer address
        CustomerAddress customerAddress = CustomerAddress.builder()
                .user(savedUser)
                .label("Home")
                .country("Egypt")
                .city("Cairo")
                .street("Nasr City")
                .building("Building A")
                .apartmentNumber("Apartment 5")
                .type(AddressType.HOME)
                .isDefault(false)
                .longitude(new BigDecimal("31.2357"))
                .latitude(new BigDecimal("30.0444"))
                .build();
        CustomerAddress savedAddress = customerAddressRepository.save(customerAddress);

        this.mockMvc.perform(get(baseUrl + "/customer-addresses/{addressId}", savedAddress.getId())
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Customer address find success"))
                .andExpect(jsonPath("$.data.id").value(savedAddress.getId()));
    }

    @Test
    void shouldGetCustomerAddressByIdFailWithNoAddressFound() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/customer-addresses/{addressId}", 1)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("CustomerAddress not found"));
    }

    @Test
    void shouldDeleteCustomerAddressSuccess() throws Exception {
        // Create user
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        // Create customer address
        CustomerAddress customerAddress = CustomerAddress.builder()
                .user(savedUser)
                .label("Home")
                .country("Egypt")
                .city("Cairo")
                .street("Nasr City")
                .building("Building A")
                .apartmentNumber("Apartment 5")
                .type(AddressType.HOME)
                .isDefault(false)
                .longitude(new BigDecimal("31.2357"))
                .latitude(new BigDecimal("30.0444"))
                .build();
        CustomerAddress savedAddress = customerAddressRepository.save(customerAddress);

        this.mockMvc.perform(delete(baseUrl + "/customer-addresses/{addressId}", savedAddress.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.message").value("Customer address deleted successfully"));
    }

    @Test
    void shouldDeleteCustomerAddressFailWithNoAddressFound() throws Exception {
        this.mockMvc.perform(delete(baseUrl + "/customer-addresses/{addressId}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("CustomerAddress not found"));
    }

    @Test
    void shouldGetAllCustomerAddressesSuccess() throws Exception {
        // Create user
        User user = User.builder()
                .name("ahmed")
                .email("ahmed@test.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Ahmed@12345"))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        // Create customer addresses
        CustomerAddress address1 = CustomerAddress.builder()
                .user(savedUser)
                .label("Home")
                .country("Egypt")
                .city("Cairo")
                .street("Nasr City")
                .building("Building A")
                .apartmentNumber("Apartment 5")
                .type(AddressType.HOME)
                .isDefault(false)
                .longitude(new BigDecimal("31.2357"))
                .latitude(new BigDecimal("30.0444"))
                .build();

        CustomerAddress address2 = CustomerAddress.builder()
                .user(savedUser)
                .label("Work")
                .country("Egypt")
                .city("Alexandria")
                .street("Street 1")
                .building("Building B")
                .apartmentNumber("Apartment 10")
                .type(AddressType.WORK)
                .isDefault(false)
                .longitude(new BigDecimal("31.2000"))
                .latitude(new BigDecimal("30.0000"))
                .build();

        customerAddressRepository.save(address1);
        customerAddressRepository.save(address2);

        this.mockMvc.perform(get(baseUrl + "/customer-addresses")
                        .with(jwt().jwt(jwt -> jwt
                                .subject(savedUser.getId().toString())
                        ))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Customer addresses find success"))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }
}