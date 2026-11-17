package com.quick_bite.core_service.rbac.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MemberBranchId implements Serializable {

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "branch_id")
    private Long branchId;
}
