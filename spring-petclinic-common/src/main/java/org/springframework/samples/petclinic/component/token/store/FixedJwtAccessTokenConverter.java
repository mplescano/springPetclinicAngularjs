package org.springframework.samples.petclinic.component.token.store;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

public class FixedJwtAccessTokenConverter extends JwtAccessTokenConverter {

	public static final String TOKEN_TYPE = "token_type";
	
	public static final String REFRESH_TOKEN_ID = "rti";
	
	private JsonParser objectMapper = JsonParserFactory.create();
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
		Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
		result.setAdditionalInformation(info);
		OAuth2RefreshToken refreshToken = result.getRefreshToken();
		if (refreshToken != null) {
			DefaultOAuth2AccessToken encodedRefreshToken = new DefaultOAuth2AccessToken(accessToken);
			encodedRefreshToken.setValue(refreshToken.getValue());
			// Refresh tokens do not expire unless explicitly of the right type
			encodedRefreshToken.setExpiration(null);
			try {
				Map<String, Object> claims = objectMapper
						.parseMap(JwtHelper.decode(refreshToken.getValue()).getClaims());
				if (claims.containsKey(TOKEN_ID)) {
					encodedRefreshToken.setValue(claims.get(TOKEN_ID).toString());
				}
			}
			catch (IllegalArgumentException e) {
				//ignored
			}
			Map<String, Object> refreshTokenInfo = new LinkedHashMap<>(
					accessToken.getAdditionalInformation());
			info.put(REFRESH_TOKEN_ID, encodedRefreshToken.getValue());
			result.setAdditionalInformation(info);
			refreshTokenInfo.put(TOKEN_ID, encodedRefreshToken.getValue());
			refreshTokenInfo.put(TOKEN_TYPE, "refresh_token");
			encodedRefreshToken.setAdditionalInformation(refreshTokenInfo);
			DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken(
					encode(encodedRefreshToken, authentication));
			if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
				Date expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
				encodedRefreshToken.setExpiration(expiration);
				token = new DefaultExpiringOAuth2RefreshToken(encode(encodedRefreshToken, authentication), expiration);
			}
			result.setRefreshToken(token);
			result.setValue(encode(result, authentication));
		}
		else {
			result.setValue(encode(result, authentication));
		}
		return result;
	}

	@Override
	public boolean isRefreshToken(OAuth2AccessToken token) {
		return token.getAdditionalInformation().containsKey(TOKEN_TYPE);
	}

}
