package org.springframework.samples.petclinic.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.component.DefaultWebResponseExceptionTranslator;
import org.springframework.samples.petclinic.component.handler.RestAuthExceptionThrower;
import org.springframework.samples.petclinic.component.token.FixedDefaultTokenServices;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {

	private final DataSource dataSource;

	private final AuthenticationManager authenticationManager;
	
	private final TokenStore tokenStore;
	
	/*private final TokenEnhancer accessTokenConverter;*/
	
	private final FixedDefaultTokenServices tokenServices;
	
	private final RestAuthExceptionThrower exceptionThrower;

	public AuthServerOAuth2Config(DataSource dataSource,
			@Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager, TokenStore tokenStore,
			/*TokenEnhancer accessTokenConverter,*/ FixedDefaultTokenServices tokenServices,
			RestAuthExceptionThrower exceptionThrower) {
		this.dataSource = dataSource;
		this.authenticationManager = authenticationManager;
		this.tokenStore = tokenStore;
		/*this.accessTokenConverter = accessTokenConverter;*/
		this.tokenServices = tokenServices;
		this.exceptionThrower = exceptionThrower;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer)
			throws Exception {
		oauthServer
			.accessDeniedHandler(exceptionThrower)
			.authenticationEntryPoint(exceptionThrower)
			.basicAuthenticationEntryPoint(exceptionThrower)
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource);
		//clients.withClientDetails(clientDetailsService);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		/*final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));*/
		endpoints
				.tokenServices(tokenServices)
				.tokenStore(tokenStore)
				.authenticationManager(authenticationManager)
				//.tokenEnhancer(tokenEnhancerChain)//useless if tokenServices is filled up. 
				.pathMapping("/oauth/token", "/oauth/token/**")
				.exceptionTranslator(new DefaultWebResponseExceptionTranslator());
	}
}