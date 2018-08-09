package org.springframework.samples.petclinic.dto;

public class ResponseErrorMessage extends ResponseMessage {

	private String code;
	
	/**
	 * Bad_request, data_error: validation error, verification error
	 * UNAUTHORIZED: authentication
	 * FORBIDDEN: authorizarion
	 * INTERNAL_SERVER_ERROR: internal error, database error
	 * 
	 */
	private ErrorType type;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResponseErrorMessage(String code, ErrorType type, String message) {
		super(false, message);
		this.code = code;
		this.type = type;
	}
	
	public ResponseErrorMessage(String code, ErrorType type, String message, Object data) {
		super(false, message, data);
		this.code = code;
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public ErrorType getType() {
		return type;
	}
}