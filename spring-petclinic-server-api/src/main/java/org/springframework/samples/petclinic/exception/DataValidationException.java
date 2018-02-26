package org.springframework.samples.petclinic.exception;

public class DataValidationException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataValidationException(String message) {
		super(message);
	}
}
