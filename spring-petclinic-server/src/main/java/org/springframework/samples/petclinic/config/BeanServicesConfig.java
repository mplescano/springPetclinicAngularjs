package org.springframework.samples.petclinic.config;

import java.util.Locale;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class BeanServicesConfig {

    @Bean
    public MessageSourceAccessor buildMessageSourceAccessor(MessageSource messageSource) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        return new MessageSourceAccessor(messageSource, currentLocale);
    }
    
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }
}
