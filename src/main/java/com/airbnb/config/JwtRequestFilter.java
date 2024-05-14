package com.airbnb.config;

import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.PropertyUserRepository;
import com.airbnb.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private JwtService jwtService;

    private PropertyUserRepository userRepository;


    public JwtRequestFilter(JwtService jwtService, PropertyUserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //bearer with token
        String tokenHeader = request.getHeader("Authorization");
        if(tokenHeader!=null && tokenHeader.startsWith("Bearer")){
            //removes bearer
            //token should be with double quotes
            String token = tokenHeader.substring(8,tokenHeader.length()-1);
            //here we are just taking the token from http and printing it
            System.out.println(token);

            //got the username from the token because un is unique in db based on which I can  get the info of that particular user info so we need repo
            String username = jwtService.getUserName(token);

            //here we are getting the record of the current user from the database
            Optional<PropertyUser> opUser = userRepository.findByUsername(username);

            if(opUser.isPresent()){
                PropertyUser user = opUser.get();
                //these three lines below keeps track of current user logged in by creating a session so that spring boot uses it and understand who are the users and what info to give for whom

                //now I have to store this user detail in the session to know which user has logged in so the session will generate a unique id per user while login
                //UsernamePasswordAuthenticationToken is a built in class in which you have to supply 3 paramaters user object , credentials,role now we are not giving
                //session here is principal that is what you want to store inside the session here now we are storing the info present in user object
                //I will create a collection withonly one data by using Collections.singleton(new Simple....)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,null, Collections.singleton(new SimpleGrantedAuthority(user.getUserRole())));


               //shortcut: adani sets new world record
                authentication.setDetails(new WebAuthenticationDetails(request));

                //shortcut: security guards services

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        //automatically calls the further filters
        filterChain.doFilter(request,response);

    }
}
