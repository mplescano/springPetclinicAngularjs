package org.springframework.samples.petclinic.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.config.security.support.RestAuthExceptionThrower;
import org.springframework.samples.petclinic.config.security.support.RestAuthenticationSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String TOKEN_PREFIX = "Bearer";

    public static final String HEADER_STRING = "Authorization";


    @Bean
    public RestAuthExceptionThrower authExceptionThrower() {
    	return new RestAuthExceptionThrower();
    }
 
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
    	return new RestAuthenticationSuccessHandler();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	/*
			DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
			provider.setUserDetailsService(userDetailsService);
			if (passwordEncoder != null) {
				provider.setPasswordEncoder(passwordEncoder);
			}

			auth.authenticationProvider(provider);
    	 * */
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
    
    @Bean
    public ErrorProperties errorProperties() {
    	return new ErrorProperties();
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
        .authenticationEntryPoint(authExceptionThrower())
        	.accessDeniedHandler(authExceptionThrower())
        .and()
        .sessionManagement()
        	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()/*.accessDecisionManager(accessDecisionManager())*/
        	.antMatchers(HttpMethod.POST, "/rest/users").permitAll()
        	.antMatchers("/rest/**").authenticated()
        	.anyRequest().permitAll()
        .and()
        // We filter the api/login requests
        .addFilterBefore(buildRestLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
        // And filter other requests to check the presence of JWT in header
        .addFilterBefore(buildJWTAuthenticationFilter(), LogoutFilter.class)
        /*.formLogin()
        	.successHandler(authenticationSuccessHandler())
        	.failureHandler(authExceptionThrower())*/
        .and()
        .logout()
        	.permitAll(false)
        	.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }
 

}