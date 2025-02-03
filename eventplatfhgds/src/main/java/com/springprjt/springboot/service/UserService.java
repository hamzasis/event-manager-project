package com.springprjt.springboot.service;

import com.springprjt.springboot.dto.UserDTO;

import java.util.List;

import org.springframework.data.domain.Page;

public interface UserService {
    UserDTO signUp(UserDTO userDTO);

    boolean login(String username, String password);

    Page<UserDTO> getAllUsers(int page, int size, String sortBy);
    
    UserDTO getUserByUsername(String username);
    
}
