package org.springframework.samples.petclinic.config.security.jwt.token;

import java.security.KeyPair;

import javax.crypto.SecretKey;

public class SymmetricKey implements WrapperKey {

	private final SecretKey secretKey;
	
	public SymmetricKey(SecretKey secretKey) {
		super();
		this.secretKey = secretKey;
	}

	@Override
	public SecretKey getSecretKey() {
		return secretKey;
	}

	@Override
	public KeyPair getKeyPair() {
		throw new UnsupportedOperationException();
	}

}
