package com.indower.indtest.filters;

import java.io.IOException;
import java.util.*;

import com.indower.indtest.utils.MutableHttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// order is useful when we have more then 1 filter
@Order(1)
public class CustomFilter extends OncePerRequestFilter {

    @Autowired
    private FirebaseAuth firebaseAuth;

    private ObjectMapper mapper;

    private String[] pathsNotToFilter = { "/auth/user/u-s-e-r-d-l-t" };

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        mapper = new ObjectMapper();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        for(String x:pathsNotToFilter){
            if(path.contains(x))
                return true;
        }
        if(!path.contains("auth"))
            return true;
        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {                     
            System.out.println("hello0");
            System.out.println("hello1"+request.getHeader("id-token"));
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(request.getHeader("id-token"));
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
