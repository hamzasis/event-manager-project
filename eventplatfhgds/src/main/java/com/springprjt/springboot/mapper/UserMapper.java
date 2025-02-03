package com.springprjt.springboot.mapper;

import com.springprjt.springboot.dto.UserDTO;
import com.springprjt.springboot.model.User;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .registrationIds(user.getRegistrations() != null
                        ? user.getRegistrations().stream().map(reg -> reg.getId()).collect(Collectors.toList()) : null)
                .eventIds(user.getEvents() != null
                        ? user.getEvents().stream().map(event -> event.getId()).collect(Collectors.toList()) : null)
                .build();
    }

    public static User toUser(UserDTO userDTO) {
        if (userDTO == null) return null;

        User user = User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .password(userDTO.getPassword())
                .build();

        return user;
    }
}
