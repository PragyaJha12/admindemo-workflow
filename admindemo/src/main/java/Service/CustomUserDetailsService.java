package com.example.admindemo.security;

import com.example.admindemo.entity.User;
import com.example.admindemo.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger =
            LoggerFactory.getLogger(CustomUserDetailsService.class);


    @Autowired
    private UserRepository repo;



    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {


        logger.info("Loading user: {}", username);


        User user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );


        logger.info("User found: {}", user.getUsername());
        logger.info("Role: {}", user.getRole());


        return new org.springframework.security.core.userdetails.User(

                user.getUsername(),

                user.getPassword(),

                Collections.singletonList(

                        new SimpleGrantedAuthority(
                                user.getRole()
                        )

                )
        );
    }
}