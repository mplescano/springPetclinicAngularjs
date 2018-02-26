package org.springframework.samples.petclinic.config.security.jwt.token;

public class RawAccessJwtToken implements JwtToken {

	private String token;

	public RawAccessJwtToken(String token) {
		this.token = token;
	}

	@Override
	public String getToken() {
		return token;
	}
}