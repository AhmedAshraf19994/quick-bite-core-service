package com.quick_bite.core_service.rbac.service;

import com.quick_bite.core_service.branch.Branch;
import com.quick_bite.core_service.branch.BranchRepository;
import com.quick_bite.core_service.common.exception.BadRequestException;
import com.quick_bite.core_service.common.exception.ForbiddenActionException;
import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.rbac.dto.CreateRestaurantMemberDto;
import com.quick_bite.core_service.rbac.dto.RestaurantMemberResponseDto;
import com.quick_bite.core_service.rbac.dto.UpdateMemberBranchesDto;
import com.quick_bite.core_service.rbac.dto.UpdateRestaurantMemberDto;
import com.quick_bite.core_service.rbac.entity.MemberBranch;
import com.quick_bite.core_service.rbac.entity.RestaurantMember;
import com.quick_bite.core_service.rbac.entity.Role;
import com.quick_bite.core_service.rbac.repository.MemberBranchRepository;
import com.quick_bite.core_service.rbac.repository.RestaurantMemberRepository;
import com.quick_bite.core_service.rbac.repository.RoleRepository;
import com.quick_bite.core_service.restaurant.Restaurant;
import com.quick_bite.core_service.restaurant.RestaurantRepository;
import com.quick_bite.core_service.user.User;
import com.quick_bite.core_service.user.UserRepository;
import com.quick_bite.core_service.user.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantMemberService {

    private final RestaurantMemberRepository restaurantMemberRepository;

    private final RestaurantRepository restaurantRepository;

    private final UserRepository userRepository;

    private final BranchRepository branchRepository;

    private final RoleRepository roleRepository;

    private final MemberBranchRepository memberBranchRepository;

    @PreAuthorize("@rbacService.isRestaurantMember(#restaurantId) && @rbacService.hasResourceAccess('core:member', 'create')")
    @Transactional
    public RestaurantMemberResponseDto create (Long restaurantId, CreateRestaurantMemberDto dto) {
        //validate the restaurant exits
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        // validate that user exists
        User user = this.userRepository.findById(dto.userId())
                .orElseThrow(() -> new ObjectNotFoundException("User"));

        //check if user already member
        boolean isAlreadyMember = this.restaurantMemberRepository.existsByUserIdAndRestaurantId(dto.userId(), restaurantId);
        if (isAlreadyMember) {
            throw new BadRequestException("User is already a member of this restaurant");
        }

        //validate the branch exist and belong to the restaurant
        List<Branch> branches = this.branchRepository.findAllByRestaurant_IdAndIdIn(restaurantId, dto.branchIds());
        if (branches.size() != dto.branchIds().size()) {
            throw new ForbiddenActionException();
        }

        Role role = this.roleRepository.findByName(dto.roleName())
                .orElseThrow(() -> new ObjectNotFoundException("Role"));

        //write to restaurant members table
        RestaurantMember restaurantMember = RestaurantMember.builder()
                .user(user)
                .restaurant(restaurant)
                .role(role)
                .build() ;
        RestaurantMember savedMember = this.restaurantMemberRepository.save(restaurantMember);

        // write to  member branches table
        List<MemberBranch> memberBranches = branches.stream().map(
                branch -> {
                    return MemberBranch.builder()
                            .member(restaurantMember)
                            .branch(branch)
                            .build();
                }
        ).toList();
        this.memberBranchRepository.saveAll(memberBranches);

        // to do later send email to user to approve his membership

        // change the role of the assignee member from user to restaurant user
        user.setRole(UserRole.RESTAURANT_USER);

        // send response
        return new RestaurantMemberResponseDto(
                savedMember.getId(),
                user.getId(),
                role.getName(),
                restaurantId,
                dto.branchIds()
        );
    }

    public List<RestaurantMemberResponseDto> getAllByRestaurant(Long restaurantId) {
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant"));

        List<RestaurantMember> members = this.restaurantMemberRepository.findByRestaurant(restaurant);
        return members.stream()
                .map(member -> new RestaurantMemberResponseDto(
                        member.getId(),
                        member.getUser().getId(),
                        member.getRole().getName(),
                        member.getRestaurant().getId(),
                        member.getBranches().stream()
                                .map(
                                        memberBranch -> memberBranch.getBranch().getId()).collect(Collectors.toSet())
                )).toList();
    }

    @PreAuthorize("@rbacService.isRestaurantMember(#restaurantId) && @rbacService.hasResourceAccess('core:member', 'create')")
    @Transactional
    public RestaurantMemberResponseDto update(Long restaurantId, Long memberId, @Valid UpdateRestaurantMemberDto dto) {
        RestaurantMember member = this.restaurantMemberRepository.findByIdAndRestaurantId(memberId, restaurantId).
                orElseThrow(() -> new ObjectNotFoundException("Restaurant member"));

        Role role = this.roleRepository.findByName(dto.role())
                .orElseThrow(() -> new ObjectNotFoundException("Role"));

        member.setRole(role);

        return  new RestaurantMemberResponseDto(
                member.getId(),
                member.getUser().getId(),
                role.getName(),
                restaurantId,
                member.getBranches().stream()
                        .map(memberBranch -> memberBranch.getBranch().getId()).collect(Collectors.toSet())
        );
    }
    @PreAuthorize("@rbacService.isRestaurantMember(#restaurantId) && @rbacService.hasResourceAccess('core:member', 'delete')")
    public void delete (Long restaurantId, Long memberId) {
        RestaurantMember member = this.restaurantMemberRepository.findByIdAndRestaurantId(memberId, restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant member"));

        if(member.getRole().getName().equals("owner")) {
            throw new ForbiddenActionException();
        }
        this.restaurantMemberRepository.delete(member);
    }

    @PreAuthorize("@rbacService.isRestaurantMember(#restaurantId) && @rbacService.hasResourceAccess('core:member', 'update')")
    @Transactional
    public void updateMemberBranches(Long restaurantId,Long memberId, UpdateMemberBranchesDto dto) {

        RestaurantMember member = this.restaurantMemberRepository.findByIdAndRestaurantId(memberId, restaurantId)
                .orElseThrow(() -> new ObjectNotFoundException("Restaurant member"));

        //validate the new branches belong to the restaurant id
        List<Branch> branches = this.branchRepository.findAllByRestaurant_IdAndIdIn(restaurantId, dto.branchIds());
        if (branches.size() != dto.branchIds().size()) {
            throw new ForbiddenActionException();
        }

        this.memberBranchRepository.deleteByMember_id(memberId);

        List<MemberBranch> memberBranches =branches.stream()
                .map(branch -> MemberBranch.builder()
                        .branch(branch)
                        .member(member)
                        .build()
                ).toList();

        this.memberBranchRepository.saveAll(memberBranches);



    }


}
