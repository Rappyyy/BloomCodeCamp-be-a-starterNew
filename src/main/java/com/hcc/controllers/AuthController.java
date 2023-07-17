package com.hcc.controllers;

import com.hcc.DTOs.AuthCredentialRequest;
import com.hcc.DTOs.AuthenticationResponse;
import com.hcc.DTOs.UserRegistrationRequest;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.repositories.AuthorityRepository;
import com.hcc.services.UserService;
import com.hcc.utils.CustomPasswordEncoder;
import com.hcc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;

    @Autowired
    AuthorityRepository authorityRepository;

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> login(@RequestBody AuthCredentialRequest request) {
        Authentication authentication = null;
        String userType = "";
        Long userId = 0L;

        try {
            // Perform authentication using the provided username and password
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                User user = (User) authentication.getPrincipal();
                userId = user.getId();
                userType = user.getAuthorities().stream().findFirst().get().getAuthority();

                // Create a response object with token, user ID, username, and userType
                String token = jwtUtil.generateToken((User) authentication.getPrincipal());
                AuthenticationResponse response = new AuthenticationResponse(token, userId, request.getUsername(), userType);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }


    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            if (jwtUtil.validateToken(token)) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
//        // Check if the username already exists in the database
//        if (userService.existsByUsername(request.getUsername())) {
//            return ResponseEntity.badRequest().body("Username already exists");
//        }
//
//        LocalDate currentDate = LocalDate.now();
//        User user = new User();
//        user.setCohortStartDate(currentDate);
//        user.setUsername(request.getUsername());
//        // Use the CustomPasswordEncoder to encode the password
//        user.setPassword(customPasswordEncoder.getPasswordEncoder().encode(request.getPassword()));
//
//        // Set the user's authorities based on the selected role
//
//        // Save the user in the database
//        userService.save(user);
//        authorityRepository.save(new Authority(request.getRole(), user));
//
//        return ResponseEntity.ok("User registered successfully");
//    }

    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        // Check if the username already exists in the database
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        LocalDate currentDate = LocalDate.now();
        User user = new User();
        user.setCohortStartDate(currentDate);
        user.setUsername(request.getUsername());
        // Use the CustomPasswordEncoder to encode the password
        user.setPassword(customPasswordEncoder.getPasswordEncoder().encode(request.getPassword()));

        // Set the user's authorities based on the selected role
//        List<Authority> authorities = new ArrayList<>();
//        authorities.add(new Authority(request.getRole(), user));
//        user.setAuthorities(authorities);

        // Save the user in the database
        userService.save(user);
        authorityRepository.save(new Authority(request.getRole(), user));

        return ResponseEntity.ok("User registered successfully");
    }


}
