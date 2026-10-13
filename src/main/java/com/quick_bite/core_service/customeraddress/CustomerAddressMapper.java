package com.quick_bite.core_service.customeraddress;

import com.quick_bite.core_service.customeraddress.dto.CreateCustomerAddressDto;
import com.quick_bite.core_service.customeraddress.dto.CustomerAddressResponseDto;
import com.quick_bite.core_service.customeraddress.dto.UpdateCustomerAddressDto;
import com.quick_bite.core_service.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerAddressMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    CustomerAddress toCustomerAddress(CreateCustomerAddressDto customerAddressRequestDto, User user);

    default AddressType map(String value) {
        return AddressType.setAddressType(value);
    }

    CustomerAddressResponseDto toCustomerAddressResponseDto(CustomerAddress customerAddress);

    void updateCustomerAddress(UpdateCustomerAddressDto dto, @MappingTarget CustomerAddress customerAddress);

}
