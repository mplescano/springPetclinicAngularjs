package org.springframework.samples.petclinic.config.mvc.support;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author mplescano
 * @see http://mwenus.blogspot.pe/2014/02/spring-security-authorization-on.html
 * @see http://mwenus.blogspot.pe/2014/03/spring-security-authorization-on.html
 * @see https://github.com/mateuszwenus/spring4-webapp/tree/1.0
 * 
 */
public class AuthorizeRequestInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AccessDecisionManager accessDecisionManager;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			AuthorizeRequest ann = handlerMethod.getMethodAnnotation(AuthorizeRequest.class);
			if (ann != null) {
				MethodInvocation methodInvocation = new SimpleMethodInvocation(handlerMethod.getBean(),
						handlerMethod.getMethod(), handlerMethod.getMethodParameters());
				checkAccess(req, resp, ann, methodInvocation);
			}
		}
		return true;
	}

	private void checkAccess(HttpServletRequest req, HttpServletResponse resp, AuthorizeRequest ann, 
			MethodInvocation methodInvocation) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		FilterInvocation fi = new FilterInvocation(req, resp, new NullFilterChain());
		ConfigAttribute configAttribute = new WebPreInvocationAttribute(ann.value(), methodInvocation);
		List<ConfigAttribute> configAttributes = Arrays.asList(configAttribute);
		if (accessDecisionManager.supports(configAttribute)) {
			accessDecisionManager.decide(authentication, fi, configAttributes);
		}
	}
}