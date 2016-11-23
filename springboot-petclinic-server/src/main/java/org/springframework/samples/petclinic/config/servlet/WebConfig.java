package org.springframework.samples.petclinic.config.servlet;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * <p>
 * The ContentNegotiatingViewResolver delegates to the
 * InternalResourceViewResolver and BeanNameViewResolver, and uses the requested
 * media type (determined by the path extension) to pick a matching view. When
 * the media type is 'text/html', it will delegate to the
 * InternalResourceViewResolver's JstlView, otherwise to the
 * BeanNameViewResolver.
 * 
 */
@Configuration
@EnableWebMvc
// POJOs labeled with the @Controller and @Service annotations are
// auto-detected.
@ComponentScan(basePackages = { "org.springframework.samples.petclinic.web" })
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// Serve static resources (*.html, ...) from src/main/webapp/
        configurer.enable();
    }

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/public/");
	}
	
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    	registry.addViewController("/").setViewName("index");
    }

	@Bean(name = "messageSource")
	@Description("Message source for this context, loaded from localized 'messages_xx' files.")
	public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
		// Files are stored inside src/main/resources
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:messages/messages");
		return messageSource;
	}

	/**
	 * Resolves specific types of exceptions to corresponding logical view names
	 * for error views.
	 * 
	 * <p>
	 * View name resolved using bean of type InternalResourceViewResolver
	 * (declared in {@link MvcViewConfig}).
	 */
	/*@Override
	public void configureHandlerExceptionResolvers(
			List<HandlerExceptionResolver> exceptionResolvers) {
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
		// results into 'WEB-INF/jsp/exception.jsp'
		exceptionResolver.setDefaultErrorView("exception");
		// needed otherwise exceptions won't be logged anywhere
		exceptionResolver.setWarnLogCategory("warn");
		exceptionResolvers.add(exceptionResolver);
	}*/

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		//converters.add(new FormHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter());
	}
	
}