package org.springframework.samples.petclinic.exception;

public class TierServiceException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TierServiceException(String message) {
		super(message);
	}
}
