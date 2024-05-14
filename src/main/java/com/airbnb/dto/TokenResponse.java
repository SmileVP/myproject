package com.airbnb.dto;
//if I send the token and put that into this object , so automatically the response entitiy converts this object into json
public class TokenResponse {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String type = "Bearer";

    private String token;
}
