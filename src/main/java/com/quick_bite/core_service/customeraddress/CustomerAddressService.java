package com.quick_bite.core_service.customeraddress;

import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.customeraddress.dto.CreateCustomerAddressDto;
import com.quick_bite.core_service.customeraddress.dto.CustomerAddressResponseDto;
import com.quick_bite.core_service.customeraddress.dto.UpdateCustomerAddressDto;
import com.quick_bite.core_service.security.CurrentUserService;
import com.quick_bite.core_service.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAddressService {

    private final CustomerAddressRepository customerAddressRepository;

    private final CustomerAddressMapper customerAddressMapper;

    private final CurrentUserService currentUserService;

    @Transactional
    public CustomerAddressResponseDto createCustomerAddress(CreateCustomerAddressDto customerAddressRequestDto) {

        // get current user from authentication context
        User currentUser = this.currentUserService.getCurrentUser();

        // convert my dto to entity
        CustomerAddress customerAddress = this.customerAddressMapper.toCustomerAddress(customerAddressRequestDto, currentUser);

        //save to database
        CustomerAddress savedCustomerAddress = this.customerAddressRepository.save(customerAddress);

        return this.customerAddressMapper.toCustomerAddressResponseDto(savedCustomerAddress);
    }

    public List<CustomerAddressResponseDto> getAllCustomerAddresses() {
        User currentUser = this.currentUserService.getCurrentUser();
        List<CustomerAddress> addresses = this.customerAddressRepository.findByUserId(currentUser.getId());
        return addresses.stream()
                .map(this.customerAddressMapper::toCustomerAddressResponseDto)
                .toList();
    }

    public CustomerAddressResponseDto getCustomerAddressById(Long id) {
        CustomerAddress customerAddress = this.customerAddressRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("CustomerAddress"));

        return this.customerAddressMapper.toCustomerAddressResponseDto(customerAddress);
    }

    @Transactional
    public CustomerAddressResponseDto updateCustomerAddressById(Long id, UpdateCustomerAddressDto dto) {
        CustomerAddress customerAddress = this.customerAddressRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("CustomerAddress"));

        this.customerAddressMapper.updateCustomerAddress(dto, customerAddress);

        CustomerAddress savedCustomerAddress = this.customerAddressRepository.save(customerAddress);

        return this.customerAddressMapper.toCustomerAddressResponseDto(savedCustomerAddress);
    }

    public void deleteCustomerAddressById(Long addressId) {
        CustomerAddress customerAddress = this.customerAddressRepository.findById(addressId)
                .orElseThrow(() -> new ObjectNotFoundException("CustomerAddress"));

        this.customerAddressRepository.delete(customerAddress);
    }

}
