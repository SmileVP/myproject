package com.airbnb.service;

import com.airbnb.dto.LoginDto;
import com.airbnb.dto.PropertyUserDto;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.PropertyUserRepository;
import com.auth0.jwt.JWT;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private PropertyUserRepository userRepository;

    public UserService(PropertyUserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    private JwtService jwtService;






    public PropertyUser addUser(PropertyUserDto propertyUserDto){
        PropertyUser user = new PropertyUser();

        user.setFirstName(propertyUserDto.getFirstName());
        user.setLastName(propertyUserDto.getLastName());
        user.setUsername(propertyUserDto.getUsername());
        user.setEmail(propertyUserDto.getEmail());
        user.setPassword(BCrypt.hashpw(propertyUserDto.getPassword(),BCrypt.gensalt(10)));
        user.setUserRole("ROLE_USER");
        PropertyUser savedUser = userRepository.save(user);
        return savedUser;
    }

    public String verifyLogin(LoginDto loginDto) {
        Optional<PropertyUser> opUser = userRepository.findByUsername(loginDto.getUsername());
        if(opUser.isPresent()){
            PropertyUser propertyUser = opUser.get();
           if(BCrypt.checkpw(loginDto.getPassword(), propertyUser.getPassword())) {
               return jwtService.generateToken(propertyUser);
           }

        }
        return null;
    }
}