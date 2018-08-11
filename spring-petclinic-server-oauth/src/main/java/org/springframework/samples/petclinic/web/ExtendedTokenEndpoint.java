package org.springframework.samples.petclinic.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.samples.petclinic.dto.ResponseMessage;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FrameworkEndpoint
public class ExtendedTokenEndpoint {
    
    protected final Log logger = LogFactory.getLog(getClass());

    private final ConsumerTokenServices tokenServices;
    
    private final TokenStore tokenStore;
    
    public ExtendedTokenEndpoint(@Qualifier("tokenServices") ConsumerTokenServices tokenServices, 
                                 @Qualifier("tokenStore") TokenStore tokenStore) {
        this.tokenServices = tokenServices;
        this.tokenStore = tokenStore;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/token")
    @ResponseBody
    public ResponseMessage revokeToken(@RequestParam("token") String token, @RequestParam("type") String tokenType) {
        if ("access_token".equals(tokenType)) {
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
            if (accessToken != null) {
                tokenStore.removeAccessToken(accessToken);
                return new ResponseMessage(true, "Successful delete token", accessToken);
            }
            return new ResponseMessage(false, "Failed delete token, verify it", accessToken);
        }
        else if ("refresh_token".equals(tokenType)) {
            OAuth2RefreshToken oauth2RefreshToken = tokenStore.readRefreshToken(token);
            if (oauth2RefreshToken != null) {
                tokenStore.removeRefreshToken(oauth2RefreshToken);
                return new ResponseMessage(true, "Successful delete token", oauth2RefreshToken);
            }
            return new ResponseMessage(false, "Failed delete refresh token, verify it", oauth2RefreshToken);
        }
        else if ("all".equals(tokenType)) {
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
            tokenServices.revokeToken(token);
            return new ResponseMessage(true, "Successful delete token", accessToken);
        }
        else {
            return new ResponseMessage(false, "Unknow token type");
        }
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/oauth/token/list")
    @ResponseBody
    public List<String> getTokens(Principal principal, @RequestParam("token") String token) {
        if (!(principal instanceof Authentication) || !((Authentication) principal).isAuthenticated()) {
            throw new InsufficientAuthenticationException(
                    "User must be authenticated with Spring Security before authorization can be completed.");
        }
    	String principalClientId = getClientId(principal);
    	String tokenClientId = getClientId(token);
    	
    	if (tokenClientId != null && principalClientId != null && tokenClientId.equals(principalClientId)) {
            List<String> tokenValues = new ArrayList<>();
            Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(principalClientId);//"sampleClientId"
            if (tokens != null) {
                for (OAuth2AccessToken itemToken : tokens) {
                    tokenValues.add(itemToken.getValue());
                }
            }
            return tokenValues;
    	}
    	return Collections.emptyList();
    }
    
	protected String getClientId(Principal principal) {
		Authentication client = (Authentication) principal;
		if (!client.isAuthenticated()) {
			throw new InsufficientAuthenticationException("The client is not authenticated.");
		}
		String clientId = client.getName();
		if (client instanceof OAuth2Authentication) {
			// Might be a client and user combined authentication
			clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
		}
		return clientId;
	}
	
    public String getClientId(String tokenValue) {
        OAuth2Authentication authentication = tokenStore.readAuthentication(tokenValue);
        if (authentication == null) {
            throw new InvalidTokenException("Invalid access token: " + tokenValue);
        }
        OAuth2Request clientAuth = authentication.getOAuth2Request();
        if (clientAuth == null) {
            throw new InvalidTokenException(
                    "Invalid access token (no client id): " + tokenValue);
        }
        return clientAuth.getClientId();
    }
}
