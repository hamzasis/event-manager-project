package com.springprjt.springboot.mapper;

import com.springprjt.springboot.dto.EventDTO;
import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.model.eventimg;
import com.springprjt.springboot.model.Registration;
import com.springprjt.springboot.model.User;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    // Method to convert Event entity to EventDTO
    public static EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        
        List<Long> eventImageIds = event.getEventimg().stream()
                                        .map(eventimg::getId) // Assuming eventimg has an 'id' field
                                        .collect(Collectors.toList());

        List<Long> registrationIds = event.getRegistrations().stream()
                                         .map(Registration::getId) // Assuming Registration has an 'id' field
                                         .collect(Collectors.toList());

        return EventDTO.builder()
                       .id(event.getId())
                       .title(event.getTitle())
                       .description(event.getDescription())
                       .location(event.getLocation())
                       .startDate(event.getStartDate())
                       .endDate(event.getEndDate())
                       .maxCapacity(event.getMaxCapacity())
                       .active(event.isActive())
                       .createdBy(event.getCreatedBy() != null ? event.getCreatedBy().getUsername() : null) // Assuming User has a 'username' field
                       .eventImages(eventImageIds)
                       .registrations(registrationIds)
                       .build();
    }

    // Method to convert EventDTO to Event entity
    public static Event toEntity(EventDTO eventDTO, User createdBy) {
        if (eventDTO == null) {
            return null;
        }

        // Assuming that 'createdBy' comes as a parameter, as EventDTO does not have the full User object
        Event event = Event.builder()
                           .id(eventDTO.getId())
                           .title(eventDTO.getTitle())
                           .description(eventDTO.getDescription())
                           .location(eventDTO.getLocation())
                           .startDate(eventDTO.getStartDate())
                           .endDate(eventDTO.getEndDate())
                           .maxCapacity(eventDTO.getMaxCapacity())
                           .active(eventDTO.isActive())
                           .createdBy(createdBy)
                           .build();

        return event;
    }
}
