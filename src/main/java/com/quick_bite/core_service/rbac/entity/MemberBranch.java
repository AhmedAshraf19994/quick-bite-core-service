package com.quick_bite.core_service.rbac.entity;

import com.quick_bite.core_service.branch.Branch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "member_branches")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MemberBranch {

    @EmbeddedId
    private MemberBranchId id;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private RestaurantMember member;

    @MapsId("branchId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @CreationTimestamp
    private Instant createdAt;
}
