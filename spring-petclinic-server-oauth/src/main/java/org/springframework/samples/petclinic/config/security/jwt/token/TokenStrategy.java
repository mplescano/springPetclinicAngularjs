package org.springframework.samples.petclinic.config.security.jwt.token;

import com.nimbusds.jwt.JWTClaimsSet;

public interface TokenStrategy {

	String encode() throws Exception;
	
	void verify() throws Exception;

	/**
	 * TODO: Wrap it in an interface class
	 * 
	 * @return
	 * @throws Exception
	 */
	JWTClaimsSet getClaims() throws Exception;
}