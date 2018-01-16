package org.springframework.samples.petclinic.config.security.jwt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.samples.petclinic.config.security.jwt.token.BuilderTokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.TokenExpiredException;
import org.springframework.samples.petclinic.config.security.jwt.token.WrapperKey;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;

@Component
public class JWTAuthorizationProvider implements AuthenticationProvider {

	private final WrapperKey wrapperKey;
	
	private final ObjectMapper mapper;

	private BuilderTokenStrategy builder;

	private final AuthTokenService authTokenService;

    @Autowired
    public JWTAuthorizationProvider(@Qualifier("jwtKey") WrapperKey jwtKey, ObjectMapper mapper,
                                    BuilderTokenStrategy builder, AuthTokenService authTokenService) {
        this.wrapperKey = jwtKey;
        this.mapper = mapper;
        this.builder = builder;
		this.authTokenService = authTokenService;
    }

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
		
		TokenStrategy tokenStrategy = null;
		try {
			tokenStrategy = builder.encrypterSigner(rawAccessToken.getToken()).withWrapperKey(wrapperKey).build();
			tokenStrategy.verify();
		} catch (Exception e) {
			throw new InsufficientAuthenticationException("Verification failed.", e);
		}

		JWTClaimsSet claims;
		String userId;
		String session;
		List<Map<String, String>> rawMapRoles;
		List<GrantedAuthority> roles = new ArrayList<>();
		PrincipalWebHolder principalWebHolder;
		try {
			claims = tokenStrategy.getClaims();
			userId = claims.getSubject();
			session = (String) claims.getClaim("session");
			rawMapRoles = mapper.readValue((String) claims.getClaim("roles"), new TypeReference<List<Map<String, String>>>() {
			});
			principalWebHolder = new PrincipalWebHolder(userId, session);
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException("Parsing of claims failed.", ex);
		}
		for (Map<String, String> rawRol : rawMapRoles) {
			roles.add(new SimpleGrantedAuthority(rawRol.get("authority")));
		}
		if(!authTokenService.existsToken(userId, rawAccessToken.getToken())){
			throw new InsufficientAuthenticationException("Invalid Token.");
		}
		if ((new Date()).after(claims.getExpirationTime())) {
			throw new TokenExpiredException("Token expired.", new JwtAuthenticationToken(principalWebHolder, roles));
		}

		return new JwtAuthenticationToken(principalWebHolder, roles);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
