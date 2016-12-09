package org.springframework.samples.petclinic.config.mvc.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.prepost.PreInvocationAttribute;

public class WebPreInvocationAttribute implements PreInvocationAttribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String attribute;
	
	private MethodInvocation methodInvocation;

	public WebPreInvocationAttribute(String attribute, MethodInvocation methodInvocation) {
		this.attribute = attribute;
		this.methodInvocation = methodInvocation;
	}
	
	@Override
	public String getAttribute() {
		return attribute;
	}

	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	
}
