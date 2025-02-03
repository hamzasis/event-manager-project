package com.springprjt.springboot.mapper;

import com.springprjt.springboot.dto.RegistrationDTO;
import com.springprjt.springboot.model.Event;
import com.springprjt.springboot.model.Registration;
import com.springprjt.springboot.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-02T14:15:24+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 23.0.1 (Eclipse Adoptium)"
)
@Component
public class RegistrationMapperImpl implements RegistrationMapper {

    @Override
    public RegistrationDTO toRegistrationDTO(Registration registration) {
        if ( registration == null ) {
            return null;
        }

        RegistrationDTO.RegistrationDTOBuilder registrationDTO = RegistrationDTO.builder();

        registrationDTO.userName( registrationUserUsername( registration ) );
        registrationDTO.eventTitle( registrationEventTitle( registration ) );
        registrationDTO.id( registration.getId() );
        registrationDTO.status( registration.getStatus() );

        return registrationDTO.build();
    }

    private String registrationUserUsername(Registration registration) {
        if ( registration == null ) {
            return null;
        }
        User user = registration.getUser();
        if ( user == null ) {
            return null;
        }
        String username = user.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    private String registrationEventTitle(Registration registration) {
        if ( registration == null ) {
            return null;
        }
        Event event = registration.getEvent();
        if ( event == null ) {
            return null;
        }
        String title = event.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}
