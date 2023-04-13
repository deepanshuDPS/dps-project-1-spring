package com.indower.indtest.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indower.indtest.utils.MutableHttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// order is useful when we have more then 1 filter
@Order(3)
public class NormalFilter extends OncePerRequestFilter {

    private ObjectMapper mapper;

    @Value("${spring.api.key}")
    private String apiKey;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        mapper = new ObjectMapper();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        if (request.getHeader("access-control-request-method") != null)
            return true;

        if (path.contains("base"))
            return true;
        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String fetchApiKey = request.getHeader("indower-api-key");
        if (fetchApiKey != null && fetchApiKey.equals(apiKey)) {
            filterChain.doFilter(new MutableHttpServletRequest(request), response);
        } else {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Bad Request");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), errorDetails);
        }

    }

}
