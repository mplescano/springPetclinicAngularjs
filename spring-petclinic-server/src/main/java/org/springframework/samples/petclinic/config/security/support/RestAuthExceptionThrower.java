package org.springframework.samples.petclinic.config.security.support;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.samples.petclinic.config.security.SessionTimeOutException;
import org.springframework.samples.petclinic.config.security.WebSecurityConfig;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class RestAuthExceptionThrower implements AuthenticationEntryPoint, AuthenticationFailureHandler, 
    AccessDeniedHandler {

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
	    
        if (request.getRequestedSessionId() != null
                && !request.isRequestedSessionIdValid()) {
            //cookie exist?
            for (Cookie cookieItem : request.getCookies()) {
                if (WebSecurityConfig.COOKIE_SESSION.equals(cookieItem.getName())) {
                    Cookie cookie = (Cookie) cookieItem.clone();
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    //clear the cookie
                    response.addCookie(cookie);//it looks like the browser doesn't clear the cookie in error request
                    //throw the exception
                    throw new SessionTimeOutException("Expired session.", authException);
                }
            }
        }
	    
		throw authException;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Consider the failure in logout
		throw exception;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (!response.isCommitted()) {
			// Put exception into request scope (perhaps of use to a view)
			//request.setAttribute(WebAttributes.ACCESS_DENIED_403,
			//		accessDeniedException);

			//accessDeniedException.getMessage()
			
			// Set the 403 status code.
			//response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			throw accessDeniedException;
		}
		
	}
}