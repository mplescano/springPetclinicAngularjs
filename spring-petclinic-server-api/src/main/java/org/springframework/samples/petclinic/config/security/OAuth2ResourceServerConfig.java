package org.springframework.samples.petclinic.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.component.VerifierTokenServices;
import org.springframework.samples.petclinic.component.handler.RestAuthExceptionThrower;
import org.springframework.samples.petclinic.component.token.FixedDefaultTokenServices;
import org.springframework.samples.petclinic.component.token.store.FixedJwtAccessTokenConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/**
	 * Appears in the response like this:
	 * WWW-Authenticate: Bearer realm="spring-oauth2-poc01"
	 */
	private static final String RESOURCE_ID = "spring-oauth2-poc01";

	private final RestAuthExceptionThrower exceptionThrower;
	
	public OAuth2ResourceServerConfig(RestAuthExceptionThrower exceptionThrower) {
		this.exceptionThrower = exceptionThrower;
	}

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		// @formatter:off
      http
      	  .csrf().disable()
          .anonymous().disable()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .authorizeRequests()
      		.antMatchers(HttpMethod.POST, "/rest/users/register").permitAll()
      		.antMatchers("/rest/**").authenticated()
      		.anyRequest().permitAll()
          .and()
          .exceptionHandling()
            .authenticationEntryPoint(exceptionThrower)
          	.accessDeniedHandler(exceptionThrower);
       	// @formatter:on        
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer config) throws Exception {
		config.resourceId(RESOURCE_ID).tokenServices(tokenServices())
		// .stateless(false)//default true
		;
	}

	@Primary
	@Bean
	public ResourceServerTokenServices tokenServices() throws Exception {
		final FixedDefaultTokenServices defaultTokenServices = new FixedDefaultTokenServices();
		defaultTokenServices.setTokenStore(new JwtTokenStore(accessTokenConverter()));
		return new VerifierTokenServices(defaultTokenServices);
	}
	
    private JwtAccessTokenConverter accessTokenConverter() throws Exception {
        JwtAccessTokenConverter converter = new FixedJwtAccessTokenConverter();
        converter.setSigningKey("123");
        converter.afterPropertiesSet();

        return converter;
    }
}