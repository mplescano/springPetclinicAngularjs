package org.springframework.samples.petclinic.dto;

public class FieldErrorMessage {

    private String field;
    
    private String defaultMessage;
    
    public FieldErrorMessage(String field, String message) {
        this.field = field;
        this.defaultMessage = message;
    }

	public String getField() {
		return field;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

}