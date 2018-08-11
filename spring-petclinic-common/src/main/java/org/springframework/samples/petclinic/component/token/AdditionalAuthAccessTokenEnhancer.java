package org.springframework.samples.petclinic.component.token;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class AdditionalAuthAccessTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
			OAuth2Authentication authentication) {
		DefaultOAuth2AccessToken defaultAccessToken = (DefaultOAuth2AccessToken) accessToken;
		Map<String, Object> additionalAuthInfo = new LinkedHashMap<>(defaultAccessToken.getAdditionalInformation());

		List<String> roles = new ArrayList<>();
		List<String> permissions = new ArrayList<>();
		additionalAuthInfo.put("roles", roles);
		additionalAuthInfo.put("permissions", permissions);
		
		Authentication userAuth = authentication.getUserAuthentication();
		userAuth.getAuthorities().forEach((grantedAuth) -> {
			if (grantedAuth.getAuthority().startsWith("ROLE_")) {
				roles.add(grantedAuth.getAuthority());
			}
			else {
				permissions.add(grantedAuth.getAuthority());
			}
		}); 

	    defaultAccessToken.setAdditionalInformation(additionalAuthInfo);
		return accessToken;
	}

}
