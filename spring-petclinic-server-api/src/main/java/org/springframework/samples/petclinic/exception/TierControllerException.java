package org.springframework.samples.petclinic.exception;

public class TierControllerException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TierControllerException(String message) {
		super(message);
	}
}
