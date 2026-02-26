package com.yasirakbal.accountservice.shared.filter;

import com.yasirakbal.accountservice.shared.constant.GeneralConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String corrId = request.getHeader(CORRELATION_ID_HEADER);

        if (corrId == null || corrId.isEmpty()) {
            corrId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(GeneralConstants.corrId, corrId);

            response.setHeader(CORRELATION_ID_HEADER, corrId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(GeneralConstants.corrId); //runs after the transaction is committed
        }
    }
}