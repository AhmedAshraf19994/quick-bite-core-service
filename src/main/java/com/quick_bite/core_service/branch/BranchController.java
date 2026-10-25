package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.branch.dto.BranchResponseDto;
import com.quick_bite.core_service.branch.dto.CreateBranchDto;
import com.quick_bite.core_service.branch.dto.UpdateBranchDto;
import com.quick_bite.core_service.branch.dto.UpdateBranchStatusDto;
import com.quick_bite.core_service.common.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}")
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/restaurants/{restaurantId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<BranchResponseDto> createBranch (
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateBranchDto dto
    ) {
        BranchResponseDto branch = this.branchService.createBranch(restaurantId, dto);
        return new Response<>(
                true,
                HttpStatus.CREATED.value(),
                "Branch created successfully",
                branch
        ) ;
    }

    @GetMapping("/restaurants/{restaurantId}/branches")
    public Response<List<BranchResponseDto>> getBranchesByRestaurantId (
            @PathVariable("restaurantId") Long restaurantId
    ) {

        List<BranchResponseDto> branches = this.branchService.getBranchesByRestaurantId(restaurantId);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Branches fetched successfully",
                branches
        );
    }

    @GetMapping("/branches/nearby")
    public Response<List<BranchResponseDto>> getNearByBranches (
            @RequestParam("lat") BigDecimal lat,
            @RequestParam("lng") BigDecimal lng
    ) {
        List<BranchResponseDto> branches = this.branchService.getNearbyBranches(lat, lng);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Nearby branches fetched successfully",
                branches
        );

    }

    @PatchMapping("/branches/{branchId}")
    public Response<BranchResponseDto> updateBranch (
            @PathVariable("branchId") Long branchId,
            @Valid @RequestBody UpdateBranchDto dto
    ) {
        BranchResponseDto branch = this.branchService.updateBranch(branchId, dto);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Branch updated successfully",
                branch );
    }

    @PatchMapping("/branches/{branchId}/status")
    public Response<BranchResponseDto> updateBranchStatus (
            @PathVariable("branchId") Long branchId,
            @Valid @RequestBody UpdateBranchStatusDto dto

    ) {
        BranchResponseDto branch = this.branchService.updateBranchStatus(branchId, dto);
        return new Response<>(
                true,
                HttpStatus.OK.value(),
                "Branch status updated successfully",
                branch );
    }


}
