package com.springprjt.springboot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.springprjt.springboot.dto.RegistrationDTO;
import com.springprjt.springboot.model.Registration;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "event.title", target = "eventTitle")
    RegistrationDTO toRegistrationDTO(Registration registration);
}

