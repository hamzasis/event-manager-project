package com.springprjt.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Encrypt passwords
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .requestMatchers(
                		"/api/users",
                		"/api/users/*",
                		"/api/users/login",
                		"/api/users/signup",
                		"/api/users/{username}",
                		"/api/event",
                		"/api/event/",
                		"/api/event/*",
                		"/api/registration/**",
                		"/api/registration/",
                		"/api/registration",
                		"/api/registration/register/{eventId}",
                		"/api/registration/update/{registrationId}",
                		"/api/registrationCount/{eventId}",
                		"/registrationCount/{eventId}")
                    .permitAll()  // Public endpoints
                .requestMatchers("/api/users/allusers")
                    .hasAnyAuthority("ADMIN", "ORGANIZER")
                    .requestMatchers(
                    		"/api/v1/auth/**",
                    		"/v2/api-docs",
                    		"/v3/api-docs",
                    		"/v3/api-docs/**",
                    		"/swagger-resources",
                    		"/swagger-resources/**",
                    		"/configuration/ui",
                    		"/configuration/security",
                    		"/swagger-ui/**",
                    		"/webjars/**",
                    		"/swagger-ui.html"
                    		).permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable(); 
        return http.build();
    }

}
