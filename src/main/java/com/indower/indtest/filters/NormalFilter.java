package com.indower.indtest.filters;

import java.io.IOException;
import java.util.Iterator;

import com.indower.indtest.utils.MutableHttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// order is useful when we have more then 1 filter
@Order(3)
public class NormalFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Iterator iterator = request.getHeaderNames().asIterator();
        // while(iterator.hasNext()){
        //     String key = (String) iterator.next();
        //     System.out.println(key+":"+request.getHeader(key));
        // }
        filterChain.doFilter(new MutableHttpServletRequest(request), response);
    }

}
