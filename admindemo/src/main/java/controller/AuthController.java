package com.example.admindemo.controller;

import com.example.admindemo.dto.AuthRequest;
import com.example.admindemo.dto.AuthResponse;
import com.example.admindemo.entity.User;
import com.example.admindemo.repository.UserRepository;
import com.example.admindemo.security.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private UserRepository repo;


    @Autowired
    private PasswordEncoder encoder;


    @Autowired
    private JwtUtil jwtUtil;



    // ✅ LOGIN (ADMIN ONLY)
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {


        logger.info("========================================");
        logger.info("[AuthController] STEP 4 -> Login Feature ");

        logger.info("[AuthController] Username received: {}",
                req.getUsername());


        // 1. Find user

        logger.info("[AuthController] STEP 5 {\n" + //
                        "    \"username\": \"rahul\",\n" + //
                        "    \"password\": \"password123\",\n" + //
                        "    \"role\": \"USER\"\n" + //
                        "}-> Searching user in database");


        User user = repo.findByUsername(req.getUsername())
                .orElseThrow(() -> {

                    logger.warn("[AuthController] User not found");

                    return new RuntimeException("Invalid credentials");
                });


        logger.info("[AuthController] User found successfully");


        // 2. Check password

        logger.info("[AuthController] Checking password");


        if (!encoder.matches(req.getPassword(), user.getPassword())) {

            logger.warn("[AuthController] Password mismatch");

            throw new RuntimeException("Invalid credentials");
        }


        logger.info("[AuthController] STEP 7 -> Password matched");



        // 🚨 3. ALLOW ONLY ADMIN

        logger.info("[AuthController] Checking user role");


        if (!"ADMIN".equals(user.getRole())) {

            logger.warn("[AuthController] Access denied. User is not ADMIN");

            throw new RuntimeException(
                    "Access denied: Only admin can login"
            );
        }


        logger.info("[AuthController] ADMIN access confirmed");



        // 4. Generate JWT

        logger.info("[AuthController] STEP 8 ->  Payment Feature generating token");


        String token = jwtUtil.generateToken(user.getUsername());


        logger.info("[AuthController] JWT token generated successfully");



        logger.info("[AuthController] STEP 9 -> Returning JWT response");
        logger.info("========================================");


        return new AuthResponse(token);
    }




    // (Optional) Register - for testing only

    @PostMapping("/register")
    public String register(@RequestBody User user) {


        logger.info("[AuthController] Register API called");

        logger.info("[AuthController] Creating admin user: {}",
                user.getUsername());


        user.setPassword(
                encoder.encode(user.getPassword())
        );


        logger.info("[AuthController] Password encrypted using BCrypt");


        // Force role to ADMIN ONLY
        user.setRole("ADMIN");


        repo.save(user);


        logger.info("[AuthController] Admin saved into database");


        return "Admin created successfully";
    }
}