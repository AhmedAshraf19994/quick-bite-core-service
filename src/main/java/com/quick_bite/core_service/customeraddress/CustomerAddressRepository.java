package com.quick_bite.core_service.customeraddress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
  List<CustomerAddress> findByUserId(Long userId);
}
