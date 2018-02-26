package org.springframework.samples.petclinic.config.security.jwt;

public class LoginRequest {

    private String username;
    
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String doi) {
        this.username = doi;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}