package org.springframework.samples.petclinic.config.security.jwt.token;

import java.security.KeyPair;

import javax.crypto.SecretKey;

public interface WrapperKey {
	
	KeyPair getKeyPair();
	
	SecretKey getSecretKey();
}