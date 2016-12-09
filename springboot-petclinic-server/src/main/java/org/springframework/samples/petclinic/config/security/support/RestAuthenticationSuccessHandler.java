package org.springframework.samples.petclinic.config.security.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.dto.ResponseMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * By default, form login will answer a successful authentication request with a
 * 301 MOVED PERMANENTLY status code; this makes sense in the context of an
 * actual login form which needs to redirect after login. For a RESTful web
 * service however, the desired response for a successful authentication should
 * be 200 OK.
 * 
 * This is done by injecting a custom authentication success handler in the form
 * login filter, to replace the default one. The new handler implements the
 * exact same login as the default
 * org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
 * with one notable difference – the redirect logic is removed
 * 
 * @author mplescano
 *
 */
@Component("restAuthenticationSuccessHandler")
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private ObjectMapper jacksonObjectMapper;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		clearAuthenticationAttributes(request);
		
		if (!response.isCommitted()) {
			//it's reached here because continueChainBeforeSuccessfulAuthentication is false
			Map<String, Object> data = new HashMap<>();
			List<String> roles = new ArrayList<>();
			List<String> perms = new ArrayList<>();
			for(GrantedAuthority authority : authentication.getAuthorities()) {
				if (authority.getAuthority().startsWith("ROLE_")) {
					roles.add(authority.getAuthority());
				}
				else {
					perms.add(authority.getAuthority());
				}
			}
			data.put("roles", roles);
			data.put("permissions", perms);
			data.put("username", authentication.getName());
			ResponseMessage message = new ResponseMessage(true, "Successful login", data);
			
			jacksonObjectMapper.writeValue(response.getWriter(), message);
		}
	}
}