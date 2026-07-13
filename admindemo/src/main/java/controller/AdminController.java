package com.example.admindemo.controller;

import com.example.admindemo.entity.User;
import com.example.admindemo.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminController {


    private static final Logger logger =
            LoggerFactory.getLogger(AdminController.class);


    @Autowired
    private UserRepository repo;



    // ==============================
    // GET ALL USERS
    // ==============================

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {

        logger.info("[AdminController] Fetching all users");

        List<User> users = repo.findAll();

        return ResponseEntity.ok(users);
    }



    // ==============================
    // GET USER BY ID
    // ==============================

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id) {


        logger.info("[AdminController] Searching user id: {}", id);


        User user = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );


        return ResponseEntity.ok(user);
    }




    // ==============================
    // CREATE USER
    // ==============================

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @RequestBody User user) {


        logger.info("[AdminController] Creating user: {}",
                user.getUsername());


        User savedUser = repo.save(user);


        return ResponseEntity.ok(savedUser);
    }





    // ==============================
    // UPDATE USER
    // ==============================

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {


        logger.info("[AdminController] Updating user id: {}", id);



        User user = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );



        user.setUsername(
                updatedUser.getUsername()
        );


        user.setPassword(
                updatedUser.getPassword()
        );


        user.setRole(
                updatedUser.getRole()
        );



        User savedUser = repo.save(user);



        return ResponseEntity.ok(savedUser);
    }





    // ==============================
    // DELETE USER
    // ==============================

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id) {


        logger.info("[AdminController] Deleting user id: {}",
                id);



        User user = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );



        repo.delete(user);



        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }

}