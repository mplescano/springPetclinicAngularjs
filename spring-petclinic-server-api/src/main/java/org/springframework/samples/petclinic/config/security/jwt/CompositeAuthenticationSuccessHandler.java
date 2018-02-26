package org.springframework.samples.petclinic.config.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class CompositeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final List<AuthenticationSuccessHandler> authenticationSuccessHandlers = new ArrayList<AuthenticationSuccessHandler>();
	
	public CompositeAuthenticationSuccessHandler(AuthenticationSuccessHandler... authenticationSuccessHandler) {
		setAuthenticationSuccessHandlers(Arrays.asList(authenticationSuccessHandler));
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		for (AuthenticationSuccessHandler authenticationSuccessHandler : authenticationSuccessHandlers) {
			authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}
	}

	public void setAuthenticationSuccessHandlers(List<AuthenticationSuccessHandler> authenticationSuccessHandler) {
		this.authenticationSuccessHandlers.addAll(authenticationSuccessHandler);
	}
}