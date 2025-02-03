package com.springprjt.springboot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.springprjt.springboot.model.eventimg; // Correct import

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description = "dedede";

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate ;

    @Column(nullable = false)
    private int maxCapacity;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<eventimg> eventimg = new ArrayList<>(); // Initialize as an empty list
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) 
    private User createdBy;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Registration> registrations = new ArrayList<>(); // Initialize as an empty list
}