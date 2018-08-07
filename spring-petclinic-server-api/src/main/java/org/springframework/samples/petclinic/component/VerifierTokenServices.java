package org.springframework.samples.petclinic.component;

import org.springframework.samples.petclinic.component.token.FixedDefaultTokenServices;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

public class VerifierTokenServices implements ResourceServerTokenServices {

    private final FixedDefaultTokenServices tokenServices;
    
    public VerifierTokenServices(FixedDefaultTokenServices tokenServices) {
        this.tokenServices = tokenServices;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
            throws AuthenticationException, InvalidTokenException {
        return tokenServices.loadAuthentication(accessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

}
