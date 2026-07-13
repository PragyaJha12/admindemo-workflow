package com.example.admindemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("=================================================");
        logger.info("[JwtFilter] STEP 1 -> Request reached JwtFilter");
        logger.info("[JwtFilter] Request URI: {}", request.getRequestURI());
        logger.info("[JwtFilter] Request Method: {}", request.getMethod());

        String authHeader = request.getHeader("Authorization");

        logger.info("[JwtFilter] STEP 2 -> Extracting Authorization Header");
        logger.info("[JwtFilter] Authorization Header: {}", authHeader);

        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            logger.info("[JwtFilter] Bearer token found");

            token = authHeader.substring(7);

            logger.info("[JwtFilter] Extracting username from JWT");

            username = jwtUtil.extractUsername(token);

            logger.info("[JwtFilter] Username extracted: {}", username);

        } else {

            logger.info("[JwtFilter] No Bearer token found. Continuing request.");

        }

        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            logger.info("[JwtFilter] STEP 3 -> Loading UserDetails");

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            logger.info("[JwtFilter] UserDetails loaded successfully");

            logger.info("[JwtFilter] Validating JWT");

            if (jwtUtil.validateToken(token)) {

                logger.info("[JwtFilter] JWT validation successful");

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("[JwtFilter] Authentication stored in SecurityContext");

            } else {

                logger.warn("[JwtFilter] JWT validation failed");

            }
        }

        logger.info("[JwtFilter] Passing request to next filter/controller");
        logger.info("=================================================");

        filterChain.doFilter(request, response);
    }
}