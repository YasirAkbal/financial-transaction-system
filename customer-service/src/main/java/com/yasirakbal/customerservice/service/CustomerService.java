package com.yasirakbal.customerservice.service;

import com.yasirakbal.customerservice.dto.CustomerDto.CreateCustomerRequest;
import com.yasirakbal.customerservice.dto.CustomerDto.CustomerResponse;
import com.yasirakbal.customerservice.entity.Customer;
import com.yasirakbal.customerservice.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(CreateCustomerRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(UUID id) {

        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
    }
}