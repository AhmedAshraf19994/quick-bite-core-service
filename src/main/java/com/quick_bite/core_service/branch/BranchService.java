package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.branch.dto.BranchResponseDto;
import com.quick_bite.core_service.branch.dto.CreateBranchDto;
import com.quick_bite.core_service.branch.dto.UpdateBranchDto;
import com.quick_bite.core_service.branch.dto.UpdateBranchStatusDto;
import com.quick_bite.core_service.common.exception.ForbiddenActionException;
import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.restaurant.Restaurant;
import com.quick_bite.core_service.restaurant.RestaurantRepository;
import com.quick_bite.core_service.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    private final CurrentUserService currentUserService;

    private final RestaurantRepository restaurantRepository;

    private final BranchMapper branchMapper;

    public BranchResponseDto createBranch (Long restaurantId,CreateBranchDto dto) {

        // validate that only restaurant owner or admin can create the branch
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId).
                orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        long restaurantOwnerId = restaurant.getOwner().getId();
        long currentUserId = this.currentUserService.getCurrentUser().getId();
        boolean isResourceOwner = restaurantOwnerId == currentUserId;
        boolean isAdmin = this.currentUserService.isAdmin();

        if (!isResourceOwner && !isAdmin) {
            throw new ForbiddenActionException();
        }

        // mapping to branch entity
        Branch branch = this.branchMapper.toBranch(dto);
        branch.setRestaurant(restaurant);
        branch.setActive(false); // set the branch as inactive by default

        // save the branch
        Branch savedBranch = this.branchRepository.save(branch);

        // mapping to branch response dto
        return this.branchMapper.toBranchResponseDto(savedBranch);
    }

    public List<BranchResponseDto> getBranchesByRestaurantId(Long restaurantId) {
        // check for restaurant
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        // fetch branches for the restaurant
        List<Branch> branches = this.branchRepository.findByRestaurant(restaurant);

        // map to branch response dto
        return branches.stream()
                .map(this.branchMapper::toBranchResponseDto)
                .toList();
    }

    public BranchResponseDto updateBranch(Long branchId, @Valid UpdateBranchDto dto) {
        // validate that only restaurant owner or admin can update the branch
        Branch branch = this.branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundException("Branch"));

        long restaurantOwnerId = branch.getRestaurantOwnerId();
        long currentUserId = this.currentUserService.getCurrentUser().getId();
        boolean isRestaurantOwner = restaurantOwnerId == currentUserId;
        boolean isAdmin = this.currentUserService.isAdmin();
        if (!isRestaurantOwner && !isAdmin) {
            throw new ForbiddenActionException();
        }

        //update the branch
        this.branchMapper.updateBranch(branch, dto);
        Branch updatedBranch = this.branchRepository.save(branch);
        System.out.println(this.branchMapper.toBranchResponseDto(updatedBranch).isActive());
        return this.branchMapper.toBranchResponseDto(updatedBranch);
    }

    public BranchResponseDto updateBranchStatus(Long branchId, @Valid UpdateBranchStatusDto dto) {
        //validate only system admin can perform this action
        if (!this.currentUserService.isAdmin()) {
            throw new ForbiddenActionException();
        }

        // get the branch
        Branch branch = this.branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundException("Branch"));

        // update the branch status
        branch.setActive(dto.status());

        // save the branch
        Branch updatedBranch = this.branchRepository.save(branch);
        return this.branchMapper.toBranchResponseDto(updatedBranch);
    }

    public List<BranchResponseDto> getNearbyBranches(BigDecimal lat, BigDecimal lng) {
        List<Branch> nearbyBranches = this.branchRepository.findByLocation(lat, lng);
        return nearbyBranches.stream()
                .map(this.branchMapper::toBranchResponseDto)
                .toList();
    }
}
