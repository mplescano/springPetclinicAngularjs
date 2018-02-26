package org.springframework.samples.petclinic.exception;

import org.springframework.context.support.MessageSourceAccessor;

public class ApplicationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;
	
	private String userMessage;
	
	private Object payload;
	
	public ApplicationException(String message) {
		this(null, message, message, null, null);
	}
	
	public ApplicationException(String code, MessageSourceAccessor messageAccesor, String message) {
		this(code, messageAccesor.getMessage(code), message, null, null);
	}
	
	public ApplicationException(String message, Throwable cause) {
		this(null, message, message, null, cause);
	}

	public ApplicationException(String code, String message) {
		this(code, message, message, null, null);
	}
	
	public ApplicationException(String code, String userMessage, String message) {
		this(code, userMessage, message, null, null);
	}
	
	public ApplicationException(String code, String message, Throwable cause) {
		this(code, message, message, null, cause);
	}
	
	public ApplicationException(String code, String userMessage, String message, Throwable cause) {
		this(code, userMessage, message, null, cause);
	}
	
	public ApplicationException(String message, Object payload, Throwable cause) {
		this(null, message, message, payload, cause);
	}
	
	public ApplicationException(String userMessage, String message, Object payload, Throwable cause) {
		this(null, userMessage, message, payload, cause);
	}
	
	public ApplicationException(String code, String userMessage, String message, Object payload, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.userMessage = userMessage;
		this.payload = payload;
	}

	public Object getPayload() {
		return payload;
	}

	public String getCode() {
		return code;
	}

	public String getUserMessage() {
		return userMessage;
	}
	
}