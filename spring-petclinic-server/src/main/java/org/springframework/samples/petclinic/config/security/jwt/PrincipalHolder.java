package org.springframework.samples.petclinic.config.security.jwt;

public class PrincipalHolder {

	private String username;
	
	private String remoteAddr;

	public PrincipalHolder(String username, String remoteAddr) {
		this.username = username;
		this.remoteAddr = remoteAddr;
	}

	public String getUsername() {
		return username;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}
}