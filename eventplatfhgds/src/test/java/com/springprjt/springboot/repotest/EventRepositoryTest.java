package com.springprjt.springboot.repotest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.model.User;
import com.springprjt.springboot.repository.EventRepository;
import com.springprjt.springboot.repository.UserRepository;


@DataJpaTest
class EventRepositoryTest {

	@Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByCreatedBy() {
        User user = new User();
        user.setUsername("testuser");
        userRepository.save(user);

        Event event = new Event();
        event.setTitle("Test Event");
        event.setCreatedBy(user);
        eventRepository.save(event);

        List<Event> events = eventRepository.findByCreatedBy(user);
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Test Event");
    }

    @Test
    public void testFindByTitle() {
        Event event = new Event();
        event.setTitle("Test Event");
        eventRepository.save(event);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> events = eventRepository.findByTitle("Test Event", pageable);
        assertThat(events.getContent()).hasSize(1);
        assertThat(events.getContent().get(0).getTitle()).isEqualTo("Test Event");
    }

    @Test
    public void testFindByLocation() {
        Event event = new Event();
        event.setLocation("Test Location");
        eventRepository.save(event);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> events = eventRepository.findByLocation("Test Location", pageable);
        assertThat(events.getContent()).hasSize(1);
        assertThat(events.getContent().get(0).getLocation()).isEqualTo("Test Location");
    }

    @Test
    public void testFindByTitleAndLocation() {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setLocation("Test Location");
        eventRepository.save(event);

        List<Event> events = eventRepository.findByTitleAndLocation("Test Event", "Test Location");
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Test Event");
        assertThat(events.get(0).getLocation()).isEqualTo("Test Location");
    }

    @Test
    public void testFindAll() {
        Event event1 = new Event();
        event1.setTitle("Event 1");
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setTitle("Event 2");
        eventRepository.save(event2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> events = eventRepository.findAll(pageable);
        assertThat(events.getContent()).hasSize(2);
    }

    @Test
    public void testFindByTitleOrLocation() {
        Event event1 = new Event();
        event1.setTitle("Event 1");
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setLocation("Location 2");
        eventRepository.save(event2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> events = eventRepository.findByTitleOrLocation("Event 1", "Location 2", pageable);
        assertThat(events.getContent()).hasSize(2);
    }

}
