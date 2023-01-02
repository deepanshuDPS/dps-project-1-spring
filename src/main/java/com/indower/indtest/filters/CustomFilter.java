package com.indower.indtest.filters;

import java.io.IOException;
import java.util.*;
import com.indower.indtest.utils.MutableHttpServletRequest;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Configuration
// order is useful when we have more then 1 filter
@Order(1)
public class CustomFilter implements Filter {

    private ObjectMapper mapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getHeader("id-token"));
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
            mutableRequest.putHeader("uid", uid);
            mutableRequest.putHeader("email", email);
            filterChain.doFilter(mutableRequest, response);   
        } catch (FirebaseAuthException e) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Not an Authourized User");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), errorDetails);
        }
    }

    @Override
    public void destroy() {

    }

}

