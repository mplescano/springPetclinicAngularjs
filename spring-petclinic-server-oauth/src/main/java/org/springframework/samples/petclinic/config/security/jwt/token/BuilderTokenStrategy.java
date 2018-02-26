package org.springframework.samples.petclinic.config.security.jwt.token;

import java.text.ParseException;

import javax.crypto.SecretKey;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class BuilderTokenStrategy {

	private JOSEObject joseObject;
	
	private WrapperKey wrapperKey;
	
	private SignedJWT unsignedJWT;
	
	private JWEHeader nestedJWEHeader;

	public BuilderTokenStrategy() {
	}
	
	public BuilderTokenStrategy encrypterRsaDefault(JWTClaimsSet claimsSet) {
		return encrypter(buildEncryptRsaHeaderDefault(), claimsSet);
	}
	
	protected JWEHeader buildEncryptRsaHeaderDefault() {
		return new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM).type(JOSEObjectType.JWT).build();
	}
	
	public BuilderTokenStrategy encrypterAesDefault(JWTClaimsSet claimsSet) {
		return encrypter(buildEncryptAesHeaderDefault(), claimsSet);
	}
	
	protected JWEHeader buildEncryptAesHeaderDefault() {
		return new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM).type(JOSEObjectType.JWT).build();
	}
	
	public BuilderTokenStrategy encrypterRsaSignerDefault(JWTClaimsSet claimsSet) throws Exception {
		unsignedJWT = new SignedJWT(buildSignRsaHeaderDefault(), claimsSet);
		nestedJWEHeader = new JWEHeader.Builder(buildEncryptRsaHeaderDefault()).contentType("JWT").build();
		joseObject = null;
		return this;
	}
	
	public BuilderTokenStrategy encrypterAesSignerDefault(JWTClaimsSet claimsSet) throws Exception {
		unsignedJWT = new SignedJWT(buildSignAesHeaderDefault(), claimsSet);
		nestedJWEHeader = new JWEHeader.Builder(buildEncryptAesHeaderDefault()).contentType("JWT").build();
		joseObject = null;
		return this;
	}

	public BuilderTokenStrategy encrypter(JWEHeader header, Payload payload) {
		joseObject = new JWEObject(new JWEHeader.Builder(header).contentType("JWT").build(), payload);
		return this;
	}
	
	public BuilderTokenStrategy encrypter(JWEHeader header, JWTClaimsSet claimsSet) {
		joseObject = new EncryptedJWT(header, claimsSet);
		return this;
	}
	
	public BuilderTokenStrategy encrypterSigner(String token) throws ParseException {
		joseObject = JWEObject.parse(token);
		unsignedJWT = null;
		nestedJWEHeader = null;
		return this;
	}
	
	public BuilderTokenStrategy encrypter(String token) throws ParseException {
		joseObject = EncryptedJWT.parse(token);
		return this;
	}
	
	public BuilderTokenStrategy signerRsaDefault(JWTClaimsSet claimsSet) {
		return signer(buildSignRsaHeaderDefault(), claimsSet);
	}
	
	protected JWSHeader buildSignRsaHeaderDefault() {
		return new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
	}
	
	protected JWSHeader buildSignAesHeaderDefault() {
		return new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
	}
	
	public BuilderTokenStrategy signer(JWSHeader header, JWTClaimsSet claimsSet) {
		joseObject = new SignedJWT(header, claimsSet);
		return this;
	}
	
	public BuilderTokenStrategy signer(String token) throws ParseException {
		joseObject = SignedJWT.parse(token);
		return this;
	}
	
	public BuilderTokenStrategy withSecretKey(SecretKey symKey) {
		this.wrapperKey = new SymmetricKey(symKey);
		return this;
	}
	
	public BuilderTokenStrategy withWrapperKey(WrapperKey wrapperKey) {
		this.wrapperKey = wrapperKey;
		return this;
	}
	
	public TokenStrategy build() {
		try {
			if (wrapperKey instanceof SymmetricKey) {
				if (joseObject instanceof EncryptedJWT) {
					return new AESEncryptTokenStrategy(wrapperKey.getSecretKey(), (EncryptedJWT) joseObject);
				}
				else if (joseObject == null && nestedJWEHeader != null && unsignedJWT != null) {
					return new AESNestedTokenStrategy(wrapperKey.getSecretKey(), nestedJWEHeader, unsignedJWT);
				}
				else if (joseObject instanceof JWEObject && nestedJWEHeader == null && unsignedJWT == null) {
					return new AESNestedTokenStrategy(wrapperKey.getSecretKey(), (JWEObject) joseObject);
				}
			}
			return null;
		}
		finally {
			joseObject = null;
			wrapperKey = null;
			unsignedJWT = null;
			nestedJWEHeader = null;
		}

	}
}