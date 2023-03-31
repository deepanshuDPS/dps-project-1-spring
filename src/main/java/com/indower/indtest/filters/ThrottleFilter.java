package com.indower.indtest.filters;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indower.indtest.utils.MutableHttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// order is useful when we have more then 1 filter
@Order(1)
public class ThrottleFilter extends OncePerRequestFilter {

    private ObjectMapper mapper;

    @Value("${spring.throttle.others}")
    private Integer othersThrottle;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        mapper = new ObjectMapper();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        if (request.getHeader("access-control-request-method") != null)
            return true;

        // this request is concurrent with anstext for review so no need to throttle
        if (request.getPathInfo().contains("auth/reviewer"))
            return true;

        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (!request.getMethod().equalsIgnoreCase("get")) {
                String remoteAddress = request.getRemoteAddr() + "_other";
                Object redisValue = redisTemplate.opsForValue().get(remoteAddress);
                if (redisValue == null) {
                    redisTemplate.opsForValue().set(remoteAddress, "requested_others",
                            Duration.ofSeconds(othersThrottle));
                    filterChain.doFilter(new MutableHttpServletRequest(request), response);
                }
            } else if (request.getMethod().equalsIgnoreCase("get")) {
                filterChain.doFilter(new MutableHttpServletRequest(request), response);
            } else {
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("message", "Please try after some time");
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                mapper.writeValue(response.getWriter(), errorDetails);
            }
        } catch (Exception e) {
            filterChain.doFilter(new MutableHttpServletRequest(request), response);
        }
    }

}
