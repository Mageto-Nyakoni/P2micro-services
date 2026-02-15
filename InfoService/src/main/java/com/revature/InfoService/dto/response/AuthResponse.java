package com.revature.InfoService.dto.response;

public class AuthResponse {
    private String token;
    private Boolean valid;

    public AuthResponse() {
    }

    public AuthResponse(String token, Boolean valid) {
        this.token = token;
        this.valid = valid;
    }
    
    public String getToken() { return this.token; }
    public void setToken(String token) { this.token = token; }

    public Boolean getValid() { return this.valid; }
    public void setValid(Boolean valid) { this.valid = valid; }
}
