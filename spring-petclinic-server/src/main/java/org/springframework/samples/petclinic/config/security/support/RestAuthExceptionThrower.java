package org.springframework.samples.petclinic.config.security.support;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class RestAuthExceptionThrower implements AuthenticationEntryPoint, AuthenticationFailureHandler {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		/*
		 * Sending WWW-Authenticate to "FormBased" is only a means to prevent
		 * the browser from showing the basic authentication login dialog, so I
		 * can implement it in HTML. You could also set it to "Foobar" or
		 * "Custom", it just has to be different than "Basic".
		 */
		//TODO detect the accept application/json
		//response.setHeader("WWW-Authenticate", "FormBased");
		//response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		//response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		throw authException;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Consider the failure in logout
		throw exception;
	}
}