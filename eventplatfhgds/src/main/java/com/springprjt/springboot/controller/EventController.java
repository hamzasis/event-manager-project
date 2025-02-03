package com.springprjt.springboot.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springprjt.springboot.dto.EventDTO;
import com.springprjt.springboot.mapper.EventMapper;
import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.model.User;
import com.springprjt.springboot.model.eventimg;
import com.springprjt.springboot.repository.UserRepository;
import com.springprjt.springboot.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import com.springprjt.springboot.security.JWTUtil;

@RestController
@RequestMapping("/api/event")
public class EventController {
	@Autowired
    private EventService eventService;
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;
    
    @Operation(
    		description = "Post endpoint event",
    		summary = "this is a summary for creating event ",
    		responses = {
    					@ApiResponse(
    							description = "Success",
    							responseCode  = "200"
    							),
    					@ApiResponse(
    							description = "Invalid credentials",
    							responseCode  = "406"
    							),
    					@ApiResponse(
    							description = "An error occurred during login",
    							responseCode  = "500"
    							)
    					}
    		)
    @PostMapping()
    public ResponseEntity<?> createEvent(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> eventData = (Map<String, Object>) payload.get("event");
            List<Map<String, String>> eventImages = (List<Map<String, String>>) payload.get("eventImages");

            if (eventData == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Event data is required.");
            }
            
            String title = (String) eventData.get("title");
            String description = (String) eventData.get("description");
            String location = (String) eventData.get("location");
            String maxCapacity = (String) eventData.get("maxCapacity");
            
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Title is required and cannot be empty.");
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Description is required and cannot be empty.");
            }
            if (location == null || location.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Location is required and cannot be empty.");
            }
            if (eventData.get("startDate") == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("start date is required.");
            }
            if (eventData.get("endDate") == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("End date is required.");
            }
            if (maxCapacity == null || location.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("maxCapacity is required and cannot be empty.");
            }
            
            String username = jwtUtil.extractUsername(token.substring(7));
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Event event = new Event();
            event.setTitle((String) eventData.get("title"));
            event.setDescription((String) eventData.get("description"));
            event.setLocation((String) eventData.get("location"));
            event.setStartDate(LocalDateTime.parse((String) eventData.get("startDate")));
            event.setEndDate(LocalDateTime.parse((String) eventData.get("endDate")));
            event.setMaxCapacity((Integer) eventData.get("maxCapacity"));
            event.setActive((Boolean) eventData.get("active"));
            event.setCreatedBy(user);

            List<eventimg> eventImageList = new ArrayList<>();
            if (eventImages != null) {
                for (Map<String, String> imageData : eventImages) {
                    eventimg image = new eventimg();
                    image.setImageUrl(imageData.get("imageUrl"));
                    image.setEvent(event); 
                    eventImageList.add(image);
                }
            }
            event.setEventimg(eventImageList); 

            eventService.saveEvent(event);

            return ResponseEntity.ok("Event created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating event: " + e.getMessage());
        }
    }
    @Operation(
    		description = "Get endpoint for event",
    		summary = "this is a summary for retrieving an event",
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
    							description = "Invalid or malformed token.",
    							responseCode  = "401"
    							),
    					@ApiResponse(
    							description = "No events found matching the search criteria.",
    							responseCode  = "404"
    							)
    					}
    		)
    @GetMapping("/")
    public ResponseEntity<?> getEvents(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "location", required = false) String location,
    		@RequestParam(defaultValue = "0") int page, 
    		@RequestParam(defaultValue = "1") int size, 
    		@RequestParam(defaultValue = "id") String sortBy){
        try {
            String extractedToken = token.substring(7);
            String role = jwtUtil.extractRole(extractedToken);

            if ("ADMIN".equalsIgnoreCase(role) || "ORGANIZER".equalsIgnoreCase(role)) {
            	Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            	
            	Page<EventDTO> events = eventService.getEventsByNameOrLocation(title, location, pageable);
            	
                if (title == null && location == null) {
                    events = eventService.getAllEvents(pageable);
                } else {
                    events = eventService.getEventsByNameOrLocation(title, location,pageable);
                }
                if (events.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found matching the search criteria.");
                }

                return ResponseEntity.ok(events);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You do not have permission.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or malformed token.");
        }
    }
    @Operation(
    		description = "Put endpoint for event",
    		summary = "this is a summary for updating an event",
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
    							description = "Event not found.",
    							responseCode  = "404"
    							)
    					}
    		)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody EventDTO updatedEventDTO) {
        try {
            String extractedToken = token.substring(7);
            String role = jwtUtil.extractRole(extractedToken);

            if ("ADMIN".equalsIgnoreCase(role) || "ORGANIZER".equalsIgnoreCase(role)) {
                Event existingEvent = eventService.getEventEntityById(id);
                if (existingEvent != null) {
                    existingEvent.setTitle(updatedEventDTO.getTitle());
                    existingEvent.setDescription(updatedEventDTO.getDescription());
                    existingEvent.setLocation(updatedEventDTO.getLocation());
                    existingEvent.setStartDate(updatedEventDTO.getStartDate());
                    existingEvent.setEndDate(updatedEventDTO.getEndDate());
                    existingEvent.setMaxCapacity(updatedEventDTO.getMaxCapacity());
                    existingEvent.setActive(updatedEventDTO.isActive());

                    EventDTO updatedEvent = eventService.saveEvent(existingEvent);
                    return ResponseEntity.ok(updatedEvent);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Event not found.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You are not authorized to update this event.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or malformed token.");
        }
    }
    @Operation(
    		description = "Delete endpoint for event",
    		summary = "this is a summary for deleting an event",
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
    							description = "Event not found.",
    							responseCode  = "404"
    							)
    					}
    		)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @RequestHeader("Authorization") String token, 
            @PathVariable Long id) {
        try {
            String extractedToken = token.substring(7);
            String role = jwtUtil.extractRole(extractedToken);

            if ("ADMIN".equalsIgnoreCase(role) || "ORGANIZER".equalsIgnoreCase(role)) {
                Event event = eventService.getEventEntityById(id);
                if (event != null) {
                    event.setActive(false);
                    eventService.saveEvent(event);
                    
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Event not found.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You are not authorized to delete this event.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or malformed token.");
        }
    }
	
}