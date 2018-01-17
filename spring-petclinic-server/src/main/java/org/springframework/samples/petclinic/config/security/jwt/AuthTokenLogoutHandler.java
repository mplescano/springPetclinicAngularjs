package org.springframework.samples.petclinic.config.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.com.scotiabank.imagine.api.core.service.AuthTokenService;
import pe.com.scotiabank.imagine.api.login.model.endpoint.LogoutRequest;
import pe.com.scotiabank.imagine.api.login.model.endpoint.LogoutResponse;
import pe.com.scotiabank.imagine.api.login.service.LoginService;

@Component
public class AuthTokenLogoutHandler implements LogoutHandler {

	private LoginService loginService;
	
	private final ObjectMapper mapper;

	@Autowired
	public AuthTokenLogoutHandler(LoginService loginService, ObjectMapper mapper) {
		this.loginService = loginService;
		this.mapper = mapper;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		//Call BT
		LogoutRequest logoutRequest = null;
		if (authentication == null) {
			throw new InsufficientAuthenticationException("Token principal cannot be null!");
		}
		try {
			if (authentication.getPrincipal() instanceof PrincipalWebHolder) {
				PrincipalWebHolder principalWebHolder = (PrincipalWebHolder) authentication.getPrincipal();
				String userId = principalWebHolder.getUserId();
				logoutRequest = new LogoutRequest();
				logoutRequest.setUserId(userId);
			}

			LogoutResponse logoutResponse = loginService.logout(logoutRequest);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), logoutResponse);
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException("Error responding the logout", ex);
		}
	}
}