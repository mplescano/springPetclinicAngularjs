package org.springframework.samples.petclinic.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.config.security.support.CustomExpressionBasedPreInvocationAdvice;
import org.springframework.samples.petclinic.config.security.support.CustomMethodSecurityExpressionHandler;
import org.springframework.samples.petclinic.config.security.support.CustomPreInvocationAuthorizationAdviceVoter;
import org.springframework.samples.petclinic.config.security.support.PropertySecuredResourceServiceImpl;
import org.springframework.samples.petclinic.config.security.support.RestAuthenticationEntryPoint;
import org.springframework.samples.petclinic.config.security.support.RestAuthenticationSuccessHandler;
import org.springframework.samples.petclinic.config.security.support.SecuredResourceService;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
/*@ComponentScan(basePackages = { "org.springframework.samples.petclinic.config.security.support" })*/
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
 
    @Autowired
    private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
    
    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() {
      try {
        return super.authenticationManagerBean();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    
    @Autowired
    @Qualifier("userService")
    private UserDetailsManager userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	/*@Configuration
	protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

	    @Autowired
	    @Qualifier("userService")
	    private UserDetailsManager userService;

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    
		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
		}
	}*/
    
	@Configuration
	@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
	public static class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

//	    @Autowired
//	    private AuthenticationManager authenticationManager;
//
//	    @Override
//	    protected AuthenticationManager authenticationManager() {
//	        return authenticationManager;
//	    }
		
		@Autowired
		private MethodSecurityExpressionHandler expressionHandler;
		
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			return expressionHandler;
		}
		
	}

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
	
    /**
     * accessDeniedHandler is only applied when an authenticated user tries to access a resource 
     * which has not privileges
     * otherwise when the user is anonymous it's executed the authenticationEntryPoint.commence method.
     * 
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http	
        .csrf().disable()
        .exceptionHandling()
        .authenticationEntryPoint(restAuthenticationEntryPoint)
        	.accessDeniedHandler(accessDeniedHandler)
        .and()
        .authorizeRequests().accessDecisionManager(accessDecisionManager())
        	.antMatchers(HttpMethod.POST, "/rest/users").permitAll()
        	.antMatchers("/rest/**").authenticated()
        	.anyRequest().permitAll()
        .and()
        .formLogin()
        	.successHandler(restAuthenticationSuccessHandler)
        	.failureHandler(failureHandler())
        .and()
        .logout().logoutSuccessHandler(logoutSuccessHandler());
    }
 
    @Bean
    public SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }
    
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
    	return new HttpStatusReturningLogoutSuccessHandler();
    }
    
    @Bean
    public AccessDecisionManager accessDecisionManager() {
      WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
      DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
      //expressionHandler.setRoleHierarchy(roleHierarchy());
      webExpressionVoter.setExpressionHandler(expressionHandler);
      
      CustomExpressionBasedPreInvocationAdvice expressionAdvice = new CustomExpressionBasedPreInvocationAdvice();
      expressionAdvice.setExpressionHandler(createExpressionHandler());
      
      return new AffirmativeBased(Arrays.<AccessDecisionVoter<? extends Object>>asList(webExpressionVoter, 
    		  new CustomPreInvocationAuthorizationAdviceVoter(expressionAdvice)));
    }
}
