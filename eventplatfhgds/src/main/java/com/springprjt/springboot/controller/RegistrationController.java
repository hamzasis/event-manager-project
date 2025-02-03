package com.springprjt.springboot.controller;

import com.springprjt.springboot.dto.RegistrationDTO;
import com.springprjt.springboot.dto.UserDTO;
import com.springprjt.springboot.mapper.RegistrationMapper;
import com.springprjt.springboot.mapper.UserMapper;
import com.springprjt.springboot.service.RegistrationService;
import com.springprjt.springboot.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.springprjt.springboot.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springprjt.springboot.model.User;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RegistrationMapper registrationMapper; 
    
    @Operation(
    		description = "Post endpoint for registering",
    		summary = "this is a summary for resgistring to an event",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "There are no more places available for this event.",
    							responseCode  = "400"
    							),
    					@ApiResponse(
    							description = "Access denied. You do not have permission.",
    							responseCode  = "401"
    							),
    					@ApiResponse(
    							description = "Event not found.",
    							responseCode  = "500"
    							)
    					}
    		)
    @PostMapping("/{eventId}")
    public ResponseEntity<String> registerForEvent(@RequestHeader("Authorization") String token, 
                                                   @PathVariable Long eventId) {
    	try {
        String username = jwtUtil.extractUsername(token.substring(7));
        String role = jwtUtil.extractRole(token.substring(7));

        if ("ATTENDEE".equals(role)) {
            UserDTO userDTO = userService.getUserByUsername(username);
            
            User user = UserMapper.toUser(userDTO);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            
            String result = registrationService.registerUserForEvent(eventId, user.getId());
            
            if ("There are no more places available for this event.".equals(result)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
            return ResponseEntity.ok(result);
        	} else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only Attendees can register.");
        	}
    	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering for event: " + e.getMessage());
        }
    }
    @Operation(
    		description = "Put endpoint for registration",
    		summary = "this is a summary for accepting or refusing an event",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "Access denied. You do not have permission.",
    							responseCode  = "401"
    							),
    					@ApiResponse(
    							description = "Error updating registration status",
    							responseCode  = "500"
    							)
    					}
    		)
    @PutMapping("/{registrationId}")
    public ResponseEntity<String> updateRegistrationStatus(@RequestHeader("Authorization") String token, 
                                                            @PathVariable Long registrationId, 
                                                            @RequestParam String status) {
    	try {
        String role = jwtUtil.extractRole(token.substring(7));
        if (!("ORGANIZER".equals(role) || "ADMIN".equals(role))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only Organizers or Admins can update registration status.");
        }

        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            return ResponseEntity.badRequest().body("Invalid status. Must be 'APPROVED' or 'REJECTED'.");
        }

        String updateResult = registrationService.updateRegistrationStatus(registrationId, status);
        
        if ("Registration not found.".equals(updateResult)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(updateResult);
        }

        return ResponseEntity.ok(updateResult);
    	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating registration status: " + e.getMessage());
        }
    }
    @Operation(
    		description = "Get endpoint for registration",
    		summary = "this is a summary for searchinga list of registrations of an event",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "Access denied. You do not have permission.",
    							responseCode  = "401"
    							),
    					@ApiResponse(
    							description = "Not Found",
    							responseCode  = "404"
    							),
    					@ApiResponse(
    							description = "Error updating registration status",
    							responseCode  = "500"
    							)
    					}
    		)
    @GetMapping("/{eventId}")
    public ResponseEntity<Page<RegistrationDTO>> getRegistrationsForEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "2") int size, 
            @RequestParam(defaultValue = "id") String sortBy
            ) {
    	try {
        String role = jwtUtil.extractRole(token.substring(7));

        if ("ORGANIZER".equals(role) || "ADMIN".equals(role)) {
        	Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        	
            Page<RegistrationDTO> registrationDTOs = registrationService.getRegistrationsForEvent(eventId,pageable);

            if (registrationDTOs == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(registrationDTOs);
        	} else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        	}
    	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @Operation(
    		description = "Get endpoint for registration",
    		summary = "this is a summary getting total count of registrations of an event",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "Access denied. You do not have permission.",
    							responseCode  = "401"
    							),
    					@ApiResponse(
    							description = "Not Found",
    							responseCode  = "404"
    							),
    					@ApiResponse(
    							description = "Error updating registration status",
    							responseCode  = "500"
    							)
    					}
    		)
    @GetMapping("/registrationCount/{eventId}")
    public ResponseEntity<Long> getRegistrationCountForEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long eventId) {
    	try {
        String role = jwtUtil.extractRole(token.substring(7));

        if ("ADMIN".equals(role)) {
            long count = registrationService.countRegistrationsForEvent(eventId);

            if (count == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(count);
        	} else {
        		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        	}
    	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
