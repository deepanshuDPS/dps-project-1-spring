package com.indower.indtest.filters;

import java.io.IOException;

import com.indower.indtest.utils.MutableHttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// order is useful when we have more then 1 filter
@Order(1)
public class ReactAuthFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        if (!path.contains("auth")
                && request.getParameter("id-token") != null)
            return true;
        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.putHeader("id-token", request.getParameter("id-token"));
        mutableRequest.putHeader("user-id", request.getParameter("user-id"));
        filterChain.doFilter(mutableRequest, response);
    }

    @Override
    public void destroy() {

    }

}
