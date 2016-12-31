package org.springframework.samples.petclinic.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.samples.petclinic.config.security.support.CustomMethodSecurityExpressionHandler;
import org.springframework.samples.petclinic.config.security.support.PropertySecuredResourceServiceImpl;
import org.springframework.samples.petclinic.config.security.support.SecuredResourceService;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Autowired
	private ResourcePatternResolver resourceLoader;
	
	@Autowired
	private Environment env;
	
	@Bean
	public SecuredResourceService securedResourceService() {
		PropertySecuredResourceServiceImpl resourceService = new PropertySecuredResourceServiceImpl();
		resourceService.setResource(resourceLoader.getResource(env.getProperty("petclinic.secured.resources")));
		return resourceService;
	}
	
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		MethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler(securedResourceService());
		return expressionHandler;
	}
	
}
