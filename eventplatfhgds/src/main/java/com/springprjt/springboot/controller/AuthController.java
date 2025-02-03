package com.springprjt.springboot.controller;

import com.springprjt.springboot.dto.UserDTO;
import com.springprjt.springboot.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.springprjt.springboot.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management controller")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;
    
    @Operation(
    		description = "Post endpoint for Signup",
    		summary = "this is a summary for Signup get endpoint",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "User already exists",
    							responseCode  = "401"
    							)
    					}
    		)
    
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.signUp(userDTO);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User already exists");
        }
    }
    
    @Operation(
    		description = "Post endpoint for Login",
    		summary = "this is a summary for Login get endpoint",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "201"
    							),
    					@ApiResponse(
    							description = "Invalid credentials",
    							responseCode  = "405"
    							),
    					@ApiResponse(
    							description = "An error occurred during login",
    							responseCode  = "500"
    							)
    					}
    		)
    // User Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try {
            boolean isAuthenticated = userService.login(userDTO.getUsername(), userDTO.getPassword());

            if (isAuthenticated) {
                UserDTO loggedInUser = userService.getUserByUsername(userDTO.getUsername());
                String token = jwtUtil.generateToken(loggedInUser.getUsername(), loggedInUser.getRole().name());
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login.");
        }
    }
    @Operation(
    		description = "Get endpoint for Retrieving all users",
    		summary = "this is a summary for Login get endpoint",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "You are not authorized to access this resource.",
    							responseCode  = "401"
    							),
    					@ApiResponse(
    							description = "Invalid or malformed token.",
    							responseCode  = "401"
    							)
    					}
    		)
    
    // Retrieve all users
    @GetMapping("/")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token,
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "2") int size,
    		@RequestParam(defaultValue = "id") String sortby) {
        try {
            String extractedToken = token.substring(7);
            jwtUtil.printTokenContent(extractedToken);

            String role = jwtUtil.extractRole(extractedToken);
            if ("ADMIN".equalsIgnoreCase(role)) {
                Page<UserDTO> users = userService.getAllUsers(page,size,sortby);
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You are not authorized to access this resource.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or malformed token.");
        }
    }
    @Operation(
    		description = "Get endpoint for Retrieving a use by his name",
    		summary = "this is a summary for Login get endpoint",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "User not found!",
    							responseCode  = "404"
    							),
    					@ApiResponse(
    							description = "Invalid or malformed token.",
    							responseCode  = "401"
    							)
    					}
    		)
    // Get user by username
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@RequestHeader("Authorization") String token,
                                               @PathVariable("username") String username) {
        try {
            String jwtToken = token.substring(7);
            String usernameFromToken = jwtUtil.extractUsername(jwtToken);

            if (usernameFromToken == null || !jwtUtil.validateToken(jwtToken, usernameFromToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            UserDTO userDTO = userService.getUserByUsername(username);
            if (userDTO != null) {
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
    }
}
