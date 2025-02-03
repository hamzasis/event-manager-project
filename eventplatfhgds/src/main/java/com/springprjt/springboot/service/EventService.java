package com.springprjt.springboot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.springprjt.springboot.dto.EventDTO;
import com.springprjt.springboot.mapper.EventMapper;
import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.repository.EventRepository;
import com.springprjt.springboot.repository.RegistrationRepository;

import jakarta.transaction.Transactional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private EventMapper eventMapper;

    public EventDTO createEvent(Event event) {
        return eventMapper.toDTO(eventRepository.save(event)); // Use toDTO method
    }

    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(EventMapper::toDTO).collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        return event != null ? eventMapper.toDTO(event) : null; // Use toDTO method
    }
    public Event getEventEntityById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Transactional
    public EventDTO saveEvent(Event event) {
        return eventMapper.toDTO(eventRepository.save(event)); // Use toDTO method
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Page<EventDTO> getEventsByNameOrLocation(String title, String location,Pageable pageable) {
        Page<Event> eventPage;

        if (title != null && location != null) {
        	eventPage = eventRepository.findByTitleOrLocation(title, location,pageable);
        } else if (title != null) {
        	eventPage = eventRepository.findByTitle(title,pageable);
        } else if (location != null) {
        	eventPage = eventRepository.findByLocation(location,pageable);
        } else {
        	eventPage = eventRepository.findAll(pageable);
        }

        return eventPage.map(EventMapper::toDTO); 
    }

    public Map<EventDTO, Long> getEventsWithRegistrationCounts() {
        List<Event> events = eventRepository.findAll();
        Map<EventDTO, Long> eventData = new HashMap<>();

        for (Event event : events) {
            long registrationCount = registrationRepository.findByEvent(event).size();
            eventData.put(eventMapper.toDTO(event), registrationCount); // Use toDTO method
        }

        return eventData;
    }
    public boolean isEventFull(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        return event.getMaxCapacity() <= 0;
    }

    public void decrementEventCapacity(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (event.getMaxCapacity() > 0) {
            event.setMaxCapacity(event.getMaxCapacity() - 1);
            eventRepository.save(event);
        } else {
            throw new RuntimeException("Event capacity is already at 0");
        }
    }
    
    public Page<EventDTO> getAllEvents(Pageable pageable) {
        Page<Event> eventPage = eventRepository.findAll(pageable);
        return eventPage.map(event -> eventMapper.toDTO(event));
    }
    
}
