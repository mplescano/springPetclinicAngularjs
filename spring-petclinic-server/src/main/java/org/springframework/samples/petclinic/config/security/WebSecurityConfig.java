package org.springframework.samples.petclinic.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.config.security.support.RestAccessDeniedHandler;
import org.springframework.samples.petclinic.config.security.support.RestAuthenticationEntryPoint;
import org.springframework.samples.petclinic.config.security.support.RestAuthenticationSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/*@EnableWebSecurity(debug = true)*/
@Configuration
//@Order(Ordered.LOWEST_PRECEDENCE - 1) //lowest of them all
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
    	return new RestAuthenticationEntryPoint();
    }
 
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
    	return new RestAuthenticationSuccessHandler();
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
    	return new RestAccessDeniedHandler();
    }

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
        .authenticationEntryPoint(authenticationEntryPoint())
        	.accessDeniedHandler(accessDeniedHandler())
        .and()
        .authorizeRequests()/*.accessDecisionManager(accessDecisionManager())*/
        	.antMatchers(HttpMethod.POST, "/rest/users").permitAll()
        	.antMatchers("/rest/**").authenticated()
        	.anyRequest().permitAll()
        .and()
        .formLogin()
        	.successHandler(authenticationSuccessHandler())
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
}