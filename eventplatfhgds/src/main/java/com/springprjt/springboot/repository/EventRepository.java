package com.springprjt.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.model.User;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedBy(User createdBy);

    Page<Event> findByTitle(String title,Pageable pageable);

    Page<Event> findByLocation(String location,Pageable pageable);

    List<Event> findByTitleAndLocation(String title, String location);
    
    Page<Event> findAll(Pageable pageable);
    
    Page<Event> findByTitleOrLocation(String title, String location, Pageable pageable);
}