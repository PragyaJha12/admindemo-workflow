package com.example.admindemo.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
public class SecurityConfig {


    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);



    @Autowired
    private JwtFilter jwtFilter;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {


        logger.info("[SecurityConfig] Configuring Spring Security...");


        http


        // Disable CSRF
        .csrf(csrf -> {

            logger.info("[SecurityConfig] CSRF Disabled");

            csrf.disable();

        })


        // JWT is stateless
        .sessionManagement(session -> {

            logger.info("[SecurityConfig] Session Management -> STATELESS");

            session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
            );

        })


        .authorizeHttpRequests(auth -> {


            logger.info("[SecurityConfig] Configuring URL Authorization");


            auth


            // ==========================
            // PUBLIC APIs
            // ==========================


            // Login API
            .requestMatchers("/auth/login")
            .permitAll()



            // Anyone can view users
            // GET /admin/users
            .requestMatchers(
                    HttpMethod.GET,
                    "/admin/users"
            )
            .permitAll()



            // ==========================
            // ADMIN ONLY APIs
            // ==========================


            // Create user
            .requestMatchers(
                    HttpMethod.POST,
                    "/admin/users"
            )
            .hasAuthority("ADMIN")



            // Update user
            .requestMatchers(
                    HttpMethod.PUT,
                    "/admin/users/**"
            )
            .hasAuthority("ADMIN")



            // Delete user
            .requestMatchers(
                    HttpMethod.DELETE,
                    "/admin/users/**"
            )
            .hasAuthority("ADMIN")



            // Any other admin API
            .requestMatchers("/admin/**")
            .hasAuthority("ADMIN")



            // Everything else
            .anyRequest()
            .authenticated();

        })



        // JWT Filter
        .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );



        logger.info(
                "[SecurityConfig] JwtFilter added before UsernamePasswordAuthenticationFilter"
        );


        logger.info(
                "[SecurityConfig] Security Configuration Completed"
        );


        return http.build();

    }




    @Bean
    public PasswordEncoder passwordEncoder() {


        logger.info(
                "[SecurityConfig] BCryptPasswordEncoder Bean Created"
        );


        return new BCryptPasswordEncoder();

    }





    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {


        logger.info(
                "[SecurityConfig] AuthenticationManager Bean Created"
        );


        return configuration.getAuthenticationManager();

    }

}