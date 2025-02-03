package com.springprjt.springboot.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.springprjt.springboot.model.User;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
	
    private Long id;
    @NotBlank(message = "Title is required")
    @NotNull
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Location is required")
    private String location;
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    @Min(value = 1, message = "Max capacity must be at least 1")
    private int maxCapacity;
    private boolean active;
    private String createdBy;
    private List<Long> eventImages;
    private List<Long> registrations;
}
