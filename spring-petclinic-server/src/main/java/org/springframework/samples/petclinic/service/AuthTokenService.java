package org.springframework.samples.petclinic.service;

import java.time.LocalDateTime;

public interface AuthTokenService {
	
	void putToken(String userIdDomain, String token, LocalDateTime expiryDate);
	
	void removeToken(String userIdDomain);
	
	boolean existsToken(String userIdDomain, String token);
}
