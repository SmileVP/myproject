package com.airbnb.service;

import com.airbnb.entity.PropertyUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

//token generation
@Service
public class JwtService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiry.duration}")
    private int expiryTime;


    private Algorithm algorithm;

    private static final String USER_NAME = "username";

    @PostConstruct
    public void postConstruct(){
        algorithm = Algorithm.HMAC256(algorithmKey);
        System.out.println(algorithm);
    }

    public String generateToken(PropertyUser propertyUser){
       return JWT.create().withClaim(USER_NAME,propertyUser.getUsername()).
                withExpiresAt(new Date(System.currentTimeMillis()+expiryTime)).
                withIssuer(issuer).
                sign(algorithm);
    }

    public String getUserName(String token){
        //shortcut : rosy with boney v
        //it takes the token and from the token it applies the algorithm and secret key to decrypt and check the issuer and verify will check the expiry
        DecodedJWT decodedJwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);

        //if all correct in decoded token you will get the user name and return to jwt request filter
        return decodedJwt.getClaim(USER_NAME).asString();
    }

}
