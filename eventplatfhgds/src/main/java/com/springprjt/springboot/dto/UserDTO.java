package com.springprjt.springboot.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.springprjt.springboot.model.Registration;
import com.springprjt.springboot.model.Role;
import com.springprjt.springboot.model.User;
import com.springprjt.springboot.model.eventimg;

import jakarta.validation.constraints.Email;
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

public class UserDTO {
	
	
    private Long id;
    
    @NotBlank(message = "Username is required")    
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Role is required")
    private Role role;
    private List<Long> registrationIds;
    private List<Long> eventIds;
    
    public UserDTO(Long id, String username, String email, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

}