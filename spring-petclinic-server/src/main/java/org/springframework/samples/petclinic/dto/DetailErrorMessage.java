package org.springframework.samples.petclinic.dto;

public class DetailErrorMessage {

    private String code;
    
    private String message;
    
    public DetailErrorMessage(String field, String message) {
        this.code = field;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}