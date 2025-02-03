package com.springprjt.springboot.mapper;

import org.mapstruct.Mapper;

import com.springprjt.springboot.dto.EventImageDTO;
import com.springprjt.springboot.model.eventimg;

@Mapper(componentModel = "spring")
public interface EventImageMapper {
    EventImageDTO toEventImageDTO(eventimg eventImage);
}
