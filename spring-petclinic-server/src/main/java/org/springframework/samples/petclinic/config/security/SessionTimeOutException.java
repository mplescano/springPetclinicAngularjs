package org.springframework.samples.petclinic.config.security;

import org.springframework.security.core.AuthenticationException;

public class SessionTimeOutException extends AuthenticationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SessionTimeOutException(String msg) {
        super(msg);
    }
}
