package org.springframework.samples.petclinic.exception.token;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;

/**
 * @author s6026865
 *
 */
public class TokenExpiredException extends TokenException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private LocalDateTime expiryDate;

    public TokenExpiredException(String msg, LocalDateTime expiryDate, Authentication authentication) {
        super(msg, authentication);
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}