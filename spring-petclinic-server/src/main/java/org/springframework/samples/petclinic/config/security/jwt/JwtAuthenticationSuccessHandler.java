package org.springframework.samples.petclinic.config.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;

import org.joda.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.config.security.WebSecurityConfig;
import org.springframework.samples.petclinic.config.security.jwt.token.BuilderTokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.JwtAuthenticationToken;
import org.springframework.samples.petclinic.config.security.jwt.token.TokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.WrapperKey;
import org.springframework.samples.petclinic.dto.UserDto;
import org.springframework.samples.petclinic.service.AuthTokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;
	
    private final WrapperKey wrapperKey;

    private long expirationTimeInSeconds;

    private final AuthTokenService authTokenService;

    private BuilderTokenStrategy builder;

    public JwtAuthenticationSuccessHandler(ObjectMapper mapper, WrapperKey jwtKey, BuilderTokenStrategy builder,
                                            long expirationTime, AuthTokenService authTokenService) {
    	this.mapper = mapper;
    	this.wrapperKey = jwtKey;
        this.builder = builder;
        this.expirationTimeInSeconds = expirationTime;
        this.authTokenService = authTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authResult) throws IOException, ServletException {
        if (authResult instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken usernameToken = (UsernamePasswordAuthenticationToken) authResult;
            if (usernameToken.getPrincipal() instanceof UserDto) {
            	UserDto principalUser = (UserDto) usernameToken.getPrincipal();
                String jwToken = processToken(authResult, principalUser);

                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.addHeader(WebSecurityConfig.HEADER_STRING, WebSecurityConfig.TOKEN_PREFIX + " " + jwToken);
            }
        } else if (authResult instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authResult;
            if (jwtToken.getPrincipal() instanceof UserDto) {
            	UserDto prinWebHolder = (UserDto) jwtToken.getPrincipal();
                String jwToken = processToken(authResult, prinWebHolder);

                response.addHeader(WebSecurityConfig.HEADER_STRING, WebSecurityConfig.TOKEN_PREFIX + " " + jwToken);
            }
        }
    }

    private String processToken(Authentication authResult, UserDto principalUser)
            throws JsonProcessingException, IOException {
        LocalDateTime curDate = LocalDateTime.now();
        // Prepare JWT with claims set
        LocalDateTime expDate = curDate.plusSeconds((int) expirationTimeInSeconds);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(String.valueOf(principalUser.getId()))
        		.subject(principalUser.getUsername())
                .claim("roles", mapper.writeValueAsString(authResult.getAuthorities()))
                .issuer("petclinic-api")
                .expirationTime(expDate.toDate())
                .issueTime(curDate.toDate())
                .build();

        String jwToken = "";
        try {
            TokenStrategy tokenStrategy = builder.encrypterAesSignerDefault(claimsSet).withWrapperKey(wrapperKey).build();
            jwToken = tokenStrategy.encode();

            authTokenService.putToken(principalUser.getId(), jwToken, expDate);
        } catch (Exception ex) {
            throw new IOException("Failed singing RSA.", ex);
        }
        return jwToken;
    }
}