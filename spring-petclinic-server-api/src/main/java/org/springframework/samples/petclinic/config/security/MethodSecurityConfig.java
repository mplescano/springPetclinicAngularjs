package org.springframework.samples.petclinic.config.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.samples.petclinic.config.security.support.CustomMethodSecurityExpressionHandler;
import org.springframework.samples.petclinic.config.security.support.PropertySecuredResourceServiceImpl;
import org.springframework.samples.petclinic.config.security.support.SecuredResourceService;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * 
 * @author mplescano
 * @see http://stackoverflow.com/questions/20856825/autowired-property-is-null-spring-boot-configuration
 * @see https://github.com/doles/spring-boot-autowired-sample/blob/master/src/main/java/com/in1/boot/config/RootMethodSecurityConfiguration.java
 */
@Configuration
public class MethodSecurityConfig {
	
	@Bean
	public SecuredResourceService securedResourceService(ResourceLoader resourceLoader, Environment env) {
		PropertySecuredResourceServiceImpl resourceService = new PropertySecuredResourceServiceImpl();
		resourceService.setResource(resourceLoader.getResource(env.getProperty("petclinic.secured.resources")));
		return resourceService;
	}
	
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@Configuration
	protected static class InnerMethodSecurityConfig extends GlobalMethodSecurityConfiguration 
		implements BeanFactoryAware {
		
		private SecuredResourceService securedResourceService;
		
		@Bean
		public MethodSecurityExpressionHandler createExpressionHandler() {
			MethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler(securedResourceService);
			return expressionHandler;
		}

		@Override/*works! it's called twice!*/
		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
			securedResourceService = beanFactory.getBean(SecuredResourceService.class);
		}
	}
	
}