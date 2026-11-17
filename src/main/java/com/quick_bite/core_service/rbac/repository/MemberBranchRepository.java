package com.quick_bite.core_service.rbac.repository;

import com.quick_bite.core_service.rbac.entity.MemberBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface MemberBranchRepository extends JpaRepository<MemberBranch, Integer> {

    void deleteByMember_id(Long memberId);

}
