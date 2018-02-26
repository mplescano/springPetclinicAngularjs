package org.springframework.samples.petclinic.config.security.jwt.token;

import javax.crypto.SecretKey;

import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

public class AESEncryptTokenStrategy implements TokenStrategy {

	private final SecretKey secretKey;
	
	private final EncryptedJWT encryptedJWT;	
	
	public AESEncryptTokenStrategy(SecretKey secretKey, EncryptedJWT joseObject) {
		this.secretKey = secretKey;
		this.encryptedJWT = joseObject;
	}

	@Override
	public String encode() throws Exception {
		// Create an encrypter 
		JWEEncrypter encrypter = new DirectEncrypter(secretKey.getEncoded());
		// Perform encryption
		encryptedJWT.encrypt(encrypter);
		return encryptedJWT.serialize();
	}

	@Override
	public void verify() throws Exception {
		// Create a decrypter with the specified private RSA key
		JWEDecrypter decrypter = new DirectDecrypter(secretKey.getEncoded());
		// Decrypt
		encryptedJWT.decrypt(decrypter);
	}

	@Override
	public JWTClaimsSet getClaims() throws Exception {
		return encryptedJWT.getJWTClaimsSet();
	}

}
