package org.springframework.samples.petclinic.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "org.springframework.samples.petclinic.config.security" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
 
    @Autowired
    private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    
    @Autowired
    @Qualifier("userService")
    private UserDetailsManager userService;
    
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
     
    /**
     * accessDeniedHandler is only applied when an authenticated user tries to access a resource 
     * that he has not privileges
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
        .authorizeRequests()
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
    public SimpleUrlAuthenticationFailureHandler failureHandler(){
        return new SimpleUrlAuthenticationFailureHandler();
    }
    
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
    	return new HttpStatusReturningLogoutSuccessHandler();
    }
    
    
}
