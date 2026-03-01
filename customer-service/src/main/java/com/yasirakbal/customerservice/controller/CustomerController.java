package com.yasirakbal.customerservice.controller;

import com.yasirakbal.customerservice.dto.CustomerDto.CreateCustomerRequest;
import com.yasirakbal.customerservice.dto.CustomerDto.CustomerResponse;
import com.yasirakbal.customerservice.entity.Customer;
import com.yasirakbal.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request) {
        Customer createdCustomer = customerService.createCustomer(request);

        CustomerResponse response = CustomerResponse.from(createdCustomer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id) {
        Customer createdCustomer = customerService.getCustomer(id);

        CustomerResponse response = CustomerResponse.from(createdCustomer);

        return ResponseEntity.ok(response);
    }
}