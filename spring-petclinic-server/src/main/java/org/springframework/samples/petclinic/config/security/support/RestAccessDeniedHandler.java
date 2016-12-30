package org.springframework.samples.petclinic.config.security.support;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (!response.isCommitted()) {
			// Put exception into request scope (perhaps of use to a view)
			request.setAttribute(WebAttributes.ACCESS_DENIED_403,
					accessDeniedException);

			//accessDeniedException.getMessage()
			
			// Set the 403 status code.
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}

}
