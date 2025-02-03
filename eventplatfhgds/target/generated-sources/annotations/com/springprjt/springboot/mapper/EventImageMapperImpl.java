package com.springprjt.springboot.mapper;

import com.springprjt.springboot.dto.EventImageDTO;
import com.springprjt.springboot.model.eventimg;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-02T14:15:24+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 23.0.1 (Eclipse Adoptium)"
)
@Component
public class EventImageMapperImpl implements EventImageMapper {

    @Override
    public EventImageDTO toEventImageDTO(eventimg eventImage) {
        if ( eventImage == null ) {
            return null;
        }

        EventImageDTO.EventImageDTOBuilder eventImageDTO = EventImageDTO.builder();

        eventImageDTO.id( eventImage.getId() );
        eventImageDTO.imageUrl( eventImage.getImageUrl() );

        return eventImageDTO.build();
    }
}
