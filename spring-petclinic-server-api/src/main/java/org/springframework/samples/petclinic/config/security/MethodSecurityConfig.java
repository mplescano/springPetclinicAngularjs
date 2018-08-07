package org.springframework.samples.petclinic.config.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.samples.petclinic.component.PropertyResource;
import org.springframework.samples.petclinic.component.security.CustomMethodSecurityExpressionHandler;
import org.springframework.samples.petclinic.component.security.DefaultResourceServiceImpl;
import org.springframework.samples.petclinic.component.security.SecuredResourceService;
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
	
	@Bean("aclSecuredResourceService")
	public SecuredResourceService aclSecuredResourceService(@Value("${petclinic.security.acl.resources}") Resource resourceAcl) {
		return new DefaultResourceServiceImpl(new PropertyResource(resourceAcl));
	}
	
	@Bean("scopeSecuredResourceService")
	public SecuredResourceService scopeSecuredResourceService(@Value("${petclinic.security.scope.resources}") Resource resourceScope) {
		return new DefaultResourceServiceImpl(new PropertyResource(resourceScope));
	}
	
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@Configuration
	protected static class InnerMethodSecurityConfig extends GlobalMethodSecurityConfiguration 
		implements BeanFactoryAware {
		
		private SecuredResourceService aclSecuredResourceService;
		
		private SecuredResourceService scopeSecuredResourceService;
		
		@Bean
		@Override
		public MethodSecurityExpressionHandler createExpressionHandler() {
			return new CustomMethodSecurityExpressionHandler(aclSecuredResourceService, scopeSecuredResourceService);
		}

		@Override/*works! it's called twice!*/
		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
			aclSecuredResourceService = beanFactory.getBean("aclSecuredResourceService", SecuredResourceService.class);
			scopeSecuredResourceService = beanFactory.getBean("scopeSecuredResourceService", SecuredResourceService.class);
		}
	}
	
}