package org.springframework.samples.petclinic.config;

import java.util.Locale;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.samples.petclinic.component.CorsFilter;
import org.springframework.samples.petclinic.component.handler.RestAuthExceptionThrower;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanServicesConfig {

    @Bean
    public MessageSourceAccessor buildMessageSourceAccessor(MessageSource messageSource) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        return new MessageSourceAccessor(messageSource, currentLocale);
    }
    
    @Bean
    public CorsFilter corsFilter() {
    	return new CorsFilter();
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
}
