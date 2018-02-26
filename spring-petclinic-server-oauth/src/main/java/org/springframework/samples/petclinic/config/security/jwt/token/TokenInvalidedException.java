package org.springframework.samples.petclinic.config.security.jwt.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author s6026865
 *
 */
public class TokenInvalidedException extends TokenException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenInvalidedException(String msg, Authentication authentication) {
		super(msg, authentication);
	}


}