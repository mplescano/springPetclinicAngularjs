package org.springframework.samples.petclinic.exception;

public class DataVerificationException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataVerificationException(String message) {
		super(message);
	}
}