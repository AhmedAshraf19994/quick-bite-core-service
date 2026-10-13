package com.quick_bite.core_service.customeraddress;

import com.quick_bite.core_service.common.Response;
import com.quick_bite.core_service.customeraddress.dto.CreateCustomerAddressDto;
import com.quick_bite.core_service.customeraddress.dto.CustomerAddressResponseDto;
import com.quick_bite.core_service.customeraddress.dto.UpdateCustomerAddressDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/customer-addresses")
public class CustomerAddressController {

    private final CustomerAddressService customerAddressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CustomerAddressResponseDto> createCustomerAddress(@Valid @RequestBody CreateCustomerAddressDto customerAddressRequestDto) {
        CustomerAddressResponseDto customerAddress = this.customerAddressService.createCustomerAddress(customerAddressRequestDto);

        return new Response<>(
                true,
                HttpStatus.CREATED.value(),
                "Address created successfully",
                customerAddress
        );
    }

    @GetMapping
    public Response<List<CustomerAddressResponseDto>> getAllCustomerAddresses() {
        List<CustomerAddressResponseDto> addresses = this.customerAddressService.getAllCustomerAddresses();
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Customer addresses find success",
                addresses
        );
    }

    @GetMapping("/{addressId}")
    public Response<CustomerAddressResponseDto> getCustomerAddressById(@PathVariable("addressId") Long addressId) {
        CustomerAddressResponseDto address = this.customerAddressService.getCustomerAddressById(addressId);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Customer address find success",
                address
        );
    }

    @PatchMapping("/{addressId}")
    public Response<CustomerAddressResponseDto> updateCustomerAddressById(
            @PathVariable("addressId") Long addressId,
            @Valid @RequestBody UpdateCustomerAddressDto dto
    ) {
        CustomerAddressResponseDto address = this.customerAddressService.updateCustomerAddressById(addressId, dto);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Customer address updated successfully",
                address
        );
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Response<?> deleteCustomerAddressById(@PathVariable("addressId") Long addressId) {
        this.customerAddressService.deleteCustomerAddressById(addressId);
        return new Response<>(
                true,
                HttpStatus.NO_CONTENT.value(),
                "Customer address deleted successfully",
                null
        );
    }
}
