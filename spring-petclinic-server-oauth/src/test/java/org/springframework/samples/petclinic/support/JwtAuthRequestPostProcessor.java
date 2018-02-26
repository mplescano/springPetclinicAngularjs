package org.springframework.samples.petclinic.support;

import java.io.IOException;

import org.joda.time.LocalDateTime;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.samples.petclinic.config.security.WebSecurityConfig;
import org.springframework.samples.petclinic.config.security.jwt.token.BuilderTokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.JwtAuthenticationToken;
import org.springframework.samples.petclinic.config.security.jwt.token.TokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.WrapperKey;
import org.springframework.samples.petclinic.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;

public class JwtAuthRequestPostProcessor implements RequestPostProcessor {

	private final Authentication authentication;
	
	private final WrapperKey wrapperKey;
	
	public JwtAuthRequestPostProcessor(UserDetails user, WrapperKey wrapperKey) {
		UserDto userDto = (UserDto) user;
		this.wrapperKey = wrapperKey;
		this.authentication = new JwtAuthenticationToken(userDto, user.getAuthorities());
	}

	@Override
	public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
		
		request.addHeader(WebSecurityConfig.HEADER_STRING, WebSecurityConfig.TOKEN_PREFIX + " " + processToken(authentication, (UserDto) authentication.getPrincipal(), 100, wrapperKey));
		
		return request;
	}

    private String processToken(Authentication authResult, UserDto principalUser, long expirationTimeInSeconds, 
    		WrapperKey wrapperKey) {
    	BuilderTokenStrategy builder = new BuilderTokenStrategy();
    	ObjectMapper mapper = new ObjectMapper();
        LocalDateTime curDate = LocalDateTime.now();
        // Prepare JWT with claims set
        LocalDateTime expDate = curDate.plusSeconds((int) expirationTimeInSeconds);
        String jwToken = "";
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .jwtID(String.valueOf(principalUser.getId()))
            		.subject(principalUser.getUsername())
                    .claim("roles", mapper.writeValueAsString(authResult.getAuthorities()))
                    .issuer("petclinic-api")
                    .expirationTime(expDate.toDate())
                    .issueTime(curDate.toDate())
                    .build();

            TokenStrategy tokenStrategy = builder.encrypterAesSignerDefault(claimsSet).withWrapperKey(wrapperKey).build();
            jwToken = tokenStrategy.encode();
        } catch (Exception ex) {
            throw new RuntimeException("Failed singing RSA.", ex);
        }
        return jwToken;
    }
}
