package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.branch.dto.BranchResponseDto;
import com.quick_bite.core_service.branch.dto.CreateBranchDto;
import com.quick_bite.core_service.branch.dto.UpdateBranchDto;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BranchMapper {

    Branch toBranch(CreateBranchDto dto);

    @Mapping(target="isActive", source="active" )
    BranchResponseDto toBranchResponseDto(Branch branch);

    default
    CountryCode toCountryCode(String code) {
        return code == null ? null : CountryCode.setCode(code);
    }

    default
    Currency toCurrency(String currency) {
        return currency == null ? null : Currency.setCurrency(currency);
    }


    void updateBranch(@MappingTarget Branch branch, UpdateBranchDto dto);
}

