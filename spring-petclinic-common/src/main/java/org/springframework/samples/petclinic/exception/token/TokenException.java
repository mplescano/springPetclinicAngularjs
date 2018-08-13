package org.springframework.samples.petclinic.exception.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author s6026865
 *
 */
public class TokenException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String codeError;

	private Authentication authentication;
	
	public TokenException(String msg, Throwable t) {
		super(msg, t);
	}

	public TokenException(String msg, Authentication authentication) {
		super(msg);
		this.authentication = authentication;
	}
	
	public TokenException(String codeError, String msg) {
		super(msg);
		this.codeError=codeError;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public String getCodeError() {
		return codeError;
	}

	public void setCodeError(String codeError) {
		this.codeError = codeError;
	}
}