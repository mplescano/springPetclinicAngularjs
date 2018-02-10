package org.springframework.samples.petclinic.service;

import org.joda.time.LocalDateTime;
import org.springframework.samples.petclinic.model.AuthToken;

public interface AuthTokenService {

    AuthToken putToken(Integer userId, String token, LocalDateTime expiryDate);

    void removeTokenByUserId(Integer userId, LocalDateTime expiryDate);

    void removeTokenByUserId(Integer userId);

    boolean existsToken(Integer userId, String token);
}
