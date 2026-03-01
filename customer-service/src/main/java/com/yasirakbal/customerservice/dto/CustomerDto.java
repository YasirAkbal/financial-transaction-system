package com.yasirakbal.customerservice.dto;

import com.yasirakbal.customerservice.entity.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerDto {

    public record CreateCustomerRequest(String firstName, String lastName, String email) {}

    public record CustomerResponse(UUID id, String firstName, String lastName, String email, LocalDateTime createdAt) {
        public static CustomerResponse from(Customer customer) {
            return new CustomerResponse(
                    customer.getId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getCreatedAt()
            );
        }
    }
}