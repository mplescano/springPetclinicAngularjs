package org.springframework.samples.petclinic.config.security.jwt.token;

import javax.crypto.SecretKey;

import org.springframework.util.Assert;

import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class AESNestedTokenStrategy implements TokenStrategy {

	private final SecretKey secretKey;
	
	private JWEObject encryptedJWT;
	
	private SignedJWT signedJWT;
	
	private JWEHeader nestedJWEHeader;
	
	public AESNestedTokenStrategy(SecretKey rsaKey, JWEObject encryptedJWT) {
		this.secretKey = rsaKey;
		this.encryptedJWT = encryptedJWT;
		this.signedJWT = null;
		this.nestedJWEHeader = null;
	}
	
	public AESNestedTokenStrategy(SecretKey rsaKey, JWEHeader nestedJWEHeader, SignedJWT unsignedJWT) {
		this.secretKey = rsaKey;
		this.nestedJWEHeader = nestedJWEHeader;
		this.signedJWT = unsignedJWT;
		this.encryptedJWT = null;
	}
	
	@Override
	public String encode() throws Exception {
		// Create HMAC signer
		JWSSigner signer = new MACSigner(secretKey.getEncoded());
		
		signedJWT.sign(signer);
		
		// Create JWE object with signed JWT as payload
		encryptedJWT = new JWEObject(nestedJWEHeader, new Payload(signedJWT));
		
		// Perform encryption
		JWEEncrypter encrypter = new DirectEncrypter(secretKey.getEncoded());
		encryptedJWT.encrypt(encrypter);
		return encryptedJWT.serialize();
	}

	@Override
	public void verify() throws Exception {
		// Create a decrypter with the specified private RSA key
		JWEDecrypter decrypter = new DirectDecrypter(secretKey.getEncoded());
		// Decrypt
		encryptedJWT.decrypt(decrypter);
		
		// Extract payload
		signedJWT = encryptedJWT.getPayload().toSignedJWT();
		
		Assert.notNull(signedJWT, "Payload not a signed JWT");
		
		JWSVerifier verifier = new MACVerifier(secretKey.getEncoded());
		signedJWT.verify(verifier);
	}

	@Override
	public JWTClaimsSet getClaims() throws Exception {
		return signedJWT.getJWTClaimsSet();
	}
}