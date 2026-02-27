package com.yasirakbal.accountservice.shared.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS account_number_seq START WITH 1000000 INCREMENT BY 1");
    }

    public Long generate() {
        return jdbcTemplate.queryForObject("SELECT nextval('account_number_seq')", Long.class);
    }
}