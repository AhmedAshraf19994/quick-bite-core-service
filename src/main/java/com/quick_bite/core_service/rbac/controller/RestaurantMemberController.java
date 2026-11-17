package com.quick_bite.core_service.rbac.controller;

import com.quick_bite.core_service.common.Response;
import com.quick_bite.core_service.rbac.dto.CreateRestaurantMemberDto;
import com.quick_bite.core_service.rbac.dto.RestaurantMemberResponseDto;
import com.quick_bite.core_service.rbac.dto.UpdateMemberBranchesDto;
import com.quick_bite.core_service.rbac.dto.UpdateRestaurantMemberDto;
import com.quick_bite.core_service.rbac.service.RestaurantMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}")
public class RestaurantMemberController {

    private final RestaurantMemberService  restaurantMemberService;

    @PostMapping("/restaurants/{restaurantId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<RestaurantMemberResponseDto> create (
            @PathVariable("restaurantId")  Long restaurantId,
             @Valid @RequestBody CreateRestaurantMemberDto dto
    ) {
        RestaurantMemberResponseDto member = this.restaurantMemberService.create(restaurantId, dto);
        return new Response<RestaurantMemberResponseDto>(
                true,
                    HttpStatus.CREATED.value(),
                "Member created successfully",
                member
        );
    }

    // GET list all members within restaurant
    @GetMapping("/restaurants/{restaurantId}/members")
    public Response<List<RestaurantMemberResponseDto>> getAllByRestaurant (@PathVariable("restaurantId") Long restaurantId) {
        List<RestaurantMemberResponseDto> members = this.restaurantMemberService.getAllByRestaurant(restaurantId);

        return new Response<List<RestaurantMemberResponseDto>> (
                true,
                    HttpStatus.OK.value(),
                    "members fetched successfully",
                members
        );
    }

    // Patch update member role
    @PatchMapping("/restaurants/{restaurantId}/members/{memberId}")
    public Response<RestaurantMemberResponseDto> update (
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody UpdateRestaurantMemberDto dto
    ) {
    RestaurantMemberResponseDto member = this.restaurantMemberService.update(restaurantId, memberId, dto);

    return new Response<RestaurantMemberResponseDto>(
            true,
            HttpStatus.OK.value(),
            "Member updated successfully",
            member
    );
    }

    // DELETE
    @DeleteMapping("/restaurants/{restaurantId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Response<?> delete (
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("memberId") Long memberId
    ) {
        this.restaurantMemberService.delete(restaurantId, memberId);

        return new Response<>(
                true,
                HttpStatus.NO_CONTENT.value(),
                "Member deleted successfully",
                null
        );
    }

    // PUT update member branches
    @PutMapping("/restaurants/{restaurantId}/members/{memberId}/branches")
    public Response<?> updateMemberBranches (
            @PathVariable("memberId") Long memberId,
            @PathVariable("restaurantId") Long restaurantId,
            @Valid @RequestBody UpdateMemberBranchesDto dto
    ) {
        this.restaurantMemberService.updateMemberBranches(restaurantId, memberId, dto);

        return new Response<>(
                true,
                HttpStatus.NO_CONTENT.value(),
                "Member branches updated successfully",
                null
        );
    }

}
