package org.springframework.samples.petclinic.dto;

import java.io.Serializable;

public class ResponseMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean success;
	
	private String message;
	
	public ResponseMessage(boolean status, String message) {
		this.success = status;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}
}
