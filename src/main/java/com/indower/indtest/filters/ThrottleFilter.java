package com.indower.indtest.filters;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.indower.indtest.utils.MutableHttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// order is useful when we have more then 1 filter
@Order(1)
public class ThrottleFilter extends OncePerRequestFilter {


    @Value("${spring.throttle.others}")
    private Integer othersThrottle;

    private String[] pathsNotToFilter = {
            "auth/user/reviewerGoogle",
            "auth/user/reviewerEmailGAuth",
            "auth/user/reviewerFacebook",
            "auth/user/reviewerEmailFBAuth" };

    Map<String, String> pathsToThrottleSeperatly = new HashMap<String, String>() {
        {
            put("rEmail", "anon/user/reviewerEmail");
        }
    };

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        if (request.getHeader("access-control-request-method") != null)
            return true;

        // this request is concurrent with anstext for review so no need to throttle
        for (String x : pathsNotToFilter) {
            if (path.contains(x))
                return true;
        }

        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // filterChain.doFilter(new MutableHttpServletRequest(request), response);
        try {
            if (!request.getMethod().equalsIgnoreCase("get")) {
                String path = request.getRequestURI();
                String remoteIp = request.getRemoteAddr();
                // this forwarded is userful for loadbalancer servers
                if (request.getHeader("X-Forwarded-For") != null) {
                    String xFrwdFor = request.getHeader("X-Forwarded-For");
                    var ips = xFrwdFor.split(",");
                    if (ips.length > 1) {
                        remoteIp = ips[0];
                    }
                }
                String remoteAddress = remoteIp + "_other";
                for (Map.Entry<String, String> entry : pathsToThrottleSeperatly.entrySet()) {
                    if (path.contains(entry.getValue())) {
                        remoteAddress = remoteIp + "_" + entry.getKey();
                    }
                }

                Object redisValue = redisTemplate.opsForValue().get(remoteAddress);
                if (redisValue == null) {
                    redisTemplate.opsForValue().set(remoteAddress, "requested_others",
                            Duration.ofSeconds(othersThrottle));
                    filterChain.doFilter(new MutableHttpServletRequest(request), response);
                } else {
                    MutableHttpServletRequest newRequest = new MutableHttpServletRequest(request);
                    newRequest.putHeader(MutableHttpServletRequest.TOO_MANY_REQUESTS, "yes");
                    filterChain.doFilter(newRequest, response);
                }
            } else {
                filterChain.doFilter(new MutableHttpServletRequest(request), response);
            }
            // if (request.getMethod().equalsIgnoreCase("get"))
            // else {
            // Map<String, Object> errorDetails = new HashMap<>();
            // errorDetails.put("message",
            // "Too many requests, please try after " + othersThrottle + " seconds from last
            // request.");
            // response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            // response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            // mapper.writeValue(response.getWriter(), errorDetails);
            // Map<String, Object> errorDetails = new HashMap<>();
            // errorDetails.put("message", "Bad Request");
            // // mapper.writeValue(response.getWriter(), errorDetails);
            // String json = mapper.writeValueAsString(errorDetails);
            // response.setStatus(HttpStatus.BAD_REQUEST.value());
            // response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            // PrintWriter out = response.getWriter();
            // out.print(json);
            // out.flush();
            // }
        } catch (Exception e) {
            filterChain.doFilter(new MutableHttpServletRequest(request), response);
        }
    }

}
