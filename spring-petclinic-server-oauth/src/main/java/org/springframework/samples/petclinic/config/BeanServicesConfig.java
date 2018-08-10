package org.springframework.samples.petclinic.config;

import java.util.Arrays;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.samples.petclinic.component.CorsFilter;
import org.springframework.samples.petclinic.component.handler.RestAuthExceptionThrower;
import org.springframework.samples.petclinic.component.token.AdditionalAuthAccessTokenEnhancer;
import org.springframework.samples.petclinic.component.token.FixedDefaultTokenServices;
import org.springframework.samples.petclinic.component.token.store.FixedJwtAccessTokenConverter;
import org.springframework.samples.petclinic.service.JdbcUserServiceImpl;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
public class BeanServicesConfig {
	
	private final DataSource dataSource;

    public BeanServicesConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
    public MessageSourceAccessor buildMessageSourceAccessor(MessageSource messageSource) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        return new MessageSourceAccessor(messageSource, currentLocale);
    }
    
	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	/*@Bean*/
	private TokenEnhancer jwtAccessTokenConverter() {
		final FixedJwtAccessTokenConverter converter = new FixedJwtAccessTokenConverter();
		converter.setSigningKey("123");
		return converter;
	}

	@Bean
	@Primary
	public FixedDefaultTokenServices tokenServices() {
		final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new AdditionalAuthAccessTokenEnhancer() , jwtAccessTokenConverter()));
		final FixedDefaultTokenServices defaultTokenServices = new FixedDefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setReuseRefreshToken(true);
		defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
		return defaultTokenServices;
	}
	
    @Bean
    public JdbcUserServiceImpl userService() {
        JdbcUserServiceImpl userService = new JdbcUserServiceImpl();
        userService.setDataSource(dataSource);
        userService.setUsernameBasedPrimaryKey(false);
        return userService;
    }
    
    @Bean
    public RestAuthExceptionThrower authExceptionThrower() {
    	return new RestAuthExceptionThrower();
    }
    
    @Bean
    public ErrorProperties errorProperties() {
    	return new ErrorProperties();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    
    @Bean
    public CorsFilter corsFilter() {
    	return new CorsFilter();
    }
}
