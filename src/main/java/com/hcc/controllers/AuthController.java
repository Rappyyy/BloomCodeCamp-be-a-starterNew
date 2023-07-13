package com.hcc.controllers;

import com.hcc.DTOs.AuthCredentialRequest;
import com.hcc.DTOs.AuthenticationResponse;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
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

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> login(@RequestBody AuthCredentialRequest request) {
        Authentication authentication = null;
        String userType = "";
        List<Authority> authorityList = null;
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


            } else {
                return ResponseEntity.status(401).body("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // Generate JWT token and return it along with user details
        final String token = jwtUtil.generateToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new AuthenticationResponse(token, userId, request.getUsername(), userType));
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
}
