package org.springframework.samples.petclinic.dto;

public class DetailErrorMessage {

    private String code;
    
    private String message;
    
    public DetailErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}