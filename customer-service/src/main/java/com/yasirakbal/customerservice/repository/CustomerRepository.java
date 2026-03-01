package com.yasirakbal.customerservice.repository;

import com.yasirakbal.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}














