package org.springframework.samples.petclinic.service;

import org.joda.time.LocalDateTime;

public interface AuthTokenService {
	
	void putToken(Integer userId, String token, LocalDateTime expiryDate);
	
	void removeToken(Integer userId);
	
	boolean existsToken(Integer userId, String token);
}
