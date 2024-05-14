package com.airbnb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

//config file runs first when program gets started
@Configuration
public class SecurityConfig {

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http.csrf().disable().cors().disable();

    //while authenticated request is made it goes and stops at jwtRequestFilter
    //to make jwt request filter created by us to run first  before all the authorization filter so that when  we run the application in debug it will stop in jwt requestfilter
    http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);

    //if java 8 then use ant matchers and authorise Requests
        // only for signup and login no need authorization
    http.authorizeHttpRequests().anyRequest().permitAll();
//            .requestMatchers("/api/v1/users/addUser","/api/v1/users/login").permitAll()
//            .requestMatchers("/api/v1/countries/addCountry").hasRole("ADMIN")
//            .requestMatchers("/api/v1/users/profile").hasAnyRole("ADMIN","USER")
//            .anyRequest().authenticated();


    return http.build();

    }
}
