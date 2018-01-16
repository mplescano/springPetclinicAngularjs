package org.springframework.samples.petclinic.config.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import pe.com.scotiabank.imagine.api.config.SecurityConfiguration;
import pe.com.scotiabank.imagine.api.config.security.BtLogoutHandler;
import pe.com.scotiabank.imagine.api.config.security.PrincipalWebHolder;
import pe.com.scotiabank.imagine.api.config.security.token.JwtAuthenticationToken;
import pe.com.scotiabank.imagine.api.config.security.token.RawAccessJwtToken;
import pe.com.scotiabank.imagine.api.login.model.endpoint.LogoutRequest;
import pe.com.scotiabank.imagine.api.login.service.LoginService;
import pe.com.scotiabank.imagine.api.web.rest.errors.TokenExpiredException;

public class JWTAuthorizationFilter extends AbstractAuthenticationProcessingFilter {
	
	private static final Logger LOG = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

	private LoginService loginService;
	
	private RequestMatcher logoutMatcherRequest;
	
	private BtLogoutHandler btLogoutHandler;
	
	public JWTAuthorizationFilter(RequestMatcher matchers, AuthenticationManager authenticationManager, AuthenticationSuccessHandler successHandler, 
			AuthenticationFailureHandler failureHandler, LoginService loginService, RequestMatcher logoutRequest, BtLogoutHandler btLogoutHandler) {
		super(matchers);
		setAuthenticationManager(authenticationManager);
		setAuthenticationSuccessHandler(successHandler);
		setAuthenticationFailureHandler(failureHandler);
		this.loginService = loginService;
		this.logoutMatcherRequest = logoutRequest;
		this.btLogoutHandler = btLogoutHandler;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		String headerPayload = req.getHeader(SecurityConfiguration.HEADER_STRING);
		
		if (!StringUtils.hasText(headerPayload)) {
			throw new InsufficientAuthenticationException("Authorization header cannot be blank!");
		}
		if (headerPayload.length() < (SecurityConfiguration.TOKEN_PREFIX.length() + 1)) {
			throw new InsufficientAuthenticationException("Invalid authorization header size.");
		}
		String tokenPayload = headerPayload.substring(SecurityConfiguration.TOKEN_PREFIX.length() + 1, headerPayload.length());
		
		return getAuthenticationManager().authenticate(new JwtAuthenticationToken(new RawAccessJwtToken(tokenPayload)));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authResult);
		SecurityContextHolder.setContext(context);
		if (!logoutMatcherRequest.matches(request)) {
			getSuccessHandler().onAuthenticationSuccess(request, response, authResult);			
		}

		chain.doFilter(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		if (failed instanceof TokenExpiredException) {
			Authentication authentication = ((TokenExpiredException) failed).getAuthentication();
			//Call BT
			LogoutRequest logoutRequest;
			try {
				if (authentication != null && authentication.getPrincipal() instanceof PrincipalWebHolder) {
					PrincipalWebHolder principalWebHolder = (PrincipalWebHolder) authentication.getPrincipal();
					logoutRequest = new LogoutRequest();
					logoutRequest.setUserId(principalWebHolder.getUserId());
					if (logoutMatcherRequest.matches(request)) {
						SecurityContext context = SecurityContextHolder.createEmptyContext();
						context.setAuthentication(authentication);
						SecurityContextHolder.setContext(context);
						btLogoutHandler.logout(request, response, authentication);
					}
					else {
						loginService.logout(logoutRequest);
					}
				}
			} catch (InternalAuthenticationServiceException ex) {
				throw ex;
			}
			catch (Exception ex) {
				LOG.warn("Error processing the logout in the exception of JWTAuthorizationFilter", ex);
			}
		}
		
		super.unsuccessfulAuthentication(request, response, failed);
	}

}