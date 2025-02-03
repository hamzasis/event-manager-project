package com.springprjt.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.model.Registration;
import com.springprjt.springboot.model.User;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserAndEvent(User user, Event event);
    Page<Registration> findByEvent(Event event,Pageable pageable);
    List<Registration> findByEvent(Event event);
}
