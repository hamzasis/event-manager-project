package com.springprjt.springboot.impl;

import com.springprjt.springboot.dto.UserDTO;
import com.springprjt.springboot.exception.UserNotFoundException;
import com.springprjt.springboot.mapper.UserMapper;
import com.springprjt.springboot.model.User;
import com.springprjt.springboot.repository.UserRepository;
import com.springprjt.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO signUp(UserDTO userDTO) {
    	if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = UserMapper.toUser(userDTO);
        
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        User savedUser = userRepository.save(user);
        return UserMapper.toUserDTO(savedUser);
    }

    @Override
    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public Page<UserDTO> getAllUsers(int page,int size,String sortby) {
    	Pageable pageable = PageRequest.of(page, size,Sort.by(sortby)); 
    	
    	Page<User> usePage = userRepository.findAll(pageable);
    	
        return usePage.map(user -> new UserDTO(
        		user.getId(),
        		user.getUsername(),
        		user.getEmail(),
        		user.getRole()
        		));
        
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return UserMapper.toUserDTO(user);
    }
}
