package org.springframework.samples.petclinic.exception;

public class TierDomainException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TierDomainException(String message) {
		super(message);
	}
}
