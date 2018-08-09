package org.springframework.samples.petclinic.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.samples.petclinic.dto.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class OAuth2RequestPostProcessor implements RequestPostProcessor {

	private UserDetails user;

	public OAuth2RequestPostProcessor(UserDetails user) {
		this.user = user;
	}

	@Override
	public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {

		return null;
	}

	// For use with @WithOAuth2Authentication
	public OAuth2Authentication oAuth2Authentication(final String clientId,
			final String username) {
		// Look up authorities, resourceIds and scopes based on clientId
		ClientDetails client = new BaseClientDetails("clientId", "resourceIds", "scopes",
				"password", "authorities", "urn:redirectUris");
		Collection<GrantedAuthority> authorities = client.getAuthorities();
		Set<String> resourceIds = client.getResourceIds();
		Set<String> scopes = client.getScope();

		// Default values for other parameters
		Map<String, String> requestParameters = Collections.emptyMap();
		boolean approved = true;
		String redirectUrl = null;
		Set<String> responseTypes = Collections.emptySet();
		Map<String, Serializable> extensionProperties = Collections.emptyMap();

		// Create request
		OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId,
				authorities, approved, scopes, resourceIds, redirectUrl, responseTypes,
				extensionProperties);

		// Create OAuth2AccessToken
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				user, null, authorities);
		OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request,
				authenticationToken);
		return auth;
	}
}
