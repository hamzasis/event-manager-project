package com.springprjt.springboot.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.springprjt.springboot.dto.RegistrationDTO;
import com.springprjt.springboot.mapper.RegistrationMapper;
import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.model.Registration;
import com.springprjt.springboot.model.User;
import com.springprjt.springboot.repository.EventRepository;
import com.springprjt.springboot.repository.RegistrationRepository;
import com.springprjt.springboot.repository.UserRepository;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private RegistrationMapper registrationMapper; // Add the mapper

    public String registerUserForEvent(Long eventId, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);

        if (user == null || event == null) {
            return "User or Event not found.";
        }

        if (eventService.isEventFull(eventId)) {
            return "There are no more places available for this event.";
        }
        
        if (registrationRepository.existsByUserAndEvent(user, event)) {
            return "User is already registered for this event.";
        }

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus("PENDING");

        registrationRepository.save(registration);
        return "Registration successful. Pending approval.";
    }

    public String updateRegistrationStatus(Long registrationId, String status) {
        Registration registration = registrationRepository.findById(registrationId).orElse(null);

        if (registration == null) {
            return "Registration not found.";
        }

        if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
            return "Invalid status. Must be 'APPROVED' or 'REJECTED'.";
        }

        registration.setStatus(status);
        registrationRepository.save(registration);
        
        if ("APPROVED".equalsIgnoreCase(status)) {
            Event event = registration.getEvent();
            eventService.decrementEventCapacity(event.getId());
        }
        
        return "Registration status updated to " + status;
    }

    // Method to get registrations as DTO
    public Page<RegistrationDTO> getRegistrationsForEvent(Long eventId,Pageable pageable) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return null;
        }

        Page<Registration> registrations = registrationRepository.findByEvent(event,pageable);
        return registrations.map(registrationMapper::toRegistrationDTO); // Map entity to DTO
    }

    // Method to count registrations and return a number
    public long countRegistrationsForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return 0;
        }
        return registrationRepository.findByEvent(event).size();
    }
}
