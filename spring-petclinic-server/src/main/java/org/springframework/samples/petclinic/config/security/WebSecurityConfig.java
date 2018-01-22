package org.springframework.samples.petclinic.config.security;

import java.security.Key;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.config.security.jwt.AuthTokenLogoutHandler;
import org.springframework.samples.petclinic.config.security.jwt.JwtAuthenticationSuccessHandler;
import org.springframework.samples.petclinic.config.security.jwt.JwtAuthorizationFilter;
import org.springframework.samples.petclinic.config.security.jwt.JwtAuthorizationProvider;
import org.springframework.samples.petclinic.config.security.jwt.RestAuthenticationFilter;
import org.springframework.samples.petclinic.config.security.jwt.token.BuilderTokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.BuilderTokenStrategyFactory;
import org.springframework.samples.petclinic.config.security.jwt.token.SymmetricKey;
import org.springframework.samples.petclinic.config.security.jwt.token.WrapperKey;
import org.springframework.samples.petclinic.config.security.support.RestAuthExceptionThrower;
import org.springframework.samples.petclinic.config.security.support.RestAuthenticationSuccessHandler;
import org.springframework.samples.petclinic.service.AuthTokenService;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String TOKEN_PREFIX = "Bearer";

    public static final String HEADER_STRING = "Authorization";

    private static final String LOGIN_ENTRY_POINT = "/login";

    public static final String LOGOUT_ENTRY_POINT = "/logout";
    
    private static final String ERROR_ENTRY_POINT = "/error";
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    
    @Autowired
    private AuthTokenService authTokenService;
    
    @Autowired
    private AuthTokenLogoutHandler authTokenLogoutHandler;
    
    @Autowired
    private JwtAuthorizationProvider jwtAuthorizationProvider;
    
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
    	
    	auth.authenticationProvider(jwtAuthorizationProvider);
    	
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
        .addFilterBefore(buildRestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        // And filter other requests to check the presence of JWT in header
        .addFilterBefore(buildJwtAuthorizationFilter(), LogoutFilter.class)
        .logout()
        	.permitAll(false)
	        .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_ENTRY_POINT, HttpMethod.POST.toString()))
	        .addLogoutHandler(authTokenLogoutHandler)
        	.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }
    
    @Bean
    protected RestAuthenticationFilter buildRestAuthenticationFilter() throws Exception {
        return new RestAuthenticationFilter(new AntPathRequestMatcher(LOGIN_ENTRY_POINT, HttpMethod.POST.toString()), authenticationManager(), objectMapper,
                jwtAuthenticationSuccessHandler, authExceptionThrower());
    }
	
    @Bean
    protected JwtAuthorizationFilter buildJwtAuthorizationFilter() throws Exception {
    	List<RequestMatcher> matchers = new ArrayList<>();
    	//matchers.add(new AntPathRequestMatcher(PUBLIC_ENTRY_POINT));
    	matchers.add(new AntPathRequestMatcher(LOGIN_ENTRY_POINT, HttpMethod.POST.toString()));
    	//matchers.add(new AntPathRequestMatcher(SYNC_ENTRY_POINT));
        //matchers.add(new AntPathRequestMatcher(REFRESH_ENTRY_POINT));
    	matchers.add(new AntPathRequestMatcher(ERROR_ENTRY_POINT));
    	/*if (environment.acceptsProfiles("debug")) {
    		matchers.add(new AntPathRequestMatcher("/swagger-ui.html"));
    		matchers.add(new AntPathRequestMatcher("/webjars/**"));
    		matchers.add(new AntPathRequestMatcher("/swagger-resources/**"));
    		matchers.add(new AntPathRequestMatcher("/v2/api-docs"));
    		matchers.add(new AntPathRequestMatcher("/killsesion"));
    	}*/
        JwtAuthorizationFilter filter = new JwtAuthorizationFilter(
                new NegatedRequestMatcher(
                        new OrRequestMatcher(matchers.toArray(new RequestMatcher[matchers.size()]))
                ),
                authenticationManager(), jwtAuthenticationSuccessHandler, authExceptionThrower(), authTokenService,
                new AntPathRequestMatcher(LOGOUT_ENTRY_POINT, HttpMethod.POST.toString()), authTokenLogoutHandler);
        return filter;
    }

    /**
     * @param filter
     * @return
     * @see https://stackoverflow.com/questions/39314176/filter-invoke-twice-when-register-as-spring-bean
     */
    @Bean
    public FilterRegistrationBean disableFilter3BootRegistration(RestAuthenticationFilter filter3) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter3);
        registration.setEnabled(false);
        return registration;
    }
    
    /**
     * @param filter
     * @return
     * @see https://stackoverflow.com/questions/39314176/filter-invoke-twice-when-register-as-spring-bean
     */
    @Bean
    public FilterRegistrationBean disableFilter1BootRegistration(JwtAuthorizationFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }
    
    @Bean(name = "jwtKey")
    protected WrapperKey jwtKey(@Qualifier("jwtKeystore") KeyStore keyStore, @Value("${jwt.jks.key.alias.name}") String jwtJksKeyAliasName, 
    		@Value("${jwt.jks.key.password}") String jwtJksKeyPassword) throws Exception {
        return processKey(keyStore, jwtJksKeyAliasName, jwtJksKeyPassword.toCharArray());
    }
    
    @Bean(name = "jwtKeystore")
    protected KeyStore jwtKeyStore(@Value("${jwt.jks.file.path}") String jwtJksFilePath, @Value("${jwt.jks.file.password}") String jwtJksFilePassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance("JCEKS");
        keystore.load(resourceLoader.getResource(jwtJksFilePath).getInputStream(), jwtJksFilePassword.toCharArray());
        return keystore;
    }
    
    private WrapperKey processKey(KeyStore keyStore, String alias, char[] keyPass) throws Exception {
        Key key = keyStore.getKey(alias, keyPass);
        if (key instanceof SecretKey) {
        	return new SymmetricKey((SecretKey) key);
        }
        throw new IllegalArgumentException("Unknown type of key");
    }
    
    @Bean
    public JwtAuthenticationSuccessHandler buildRestSuccessHandler(ObjectMapper mapper, @Qualifier("jwtKey") WrapperKey jwtKey,
    		BuilderTokenStrategy builder, @Value("${jwt.token.expiration.time.seconds}") long expirationTimeInSeconds, AuthTokenService authTokenService) {
    	return new JwtAuthenticationSuccessHandler(mapper, jwtKey, builder, expirationTimeInSeconds, authTokenService);
    }
    
    @Bean(name = "builderTokenStrategy")
    public BuilderTokenStrategyFactory builderTokenStrategyFactoryBean() {
    	return new BuilderTokenStrategyFactory();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BuilderTokenStrategy builderTokenStrategy() throws Exception {
        return builderTokenStrategyFactoryBean().getObject();
    }
    
    @Bean
    public AuthTokenLogoutHandler buildAuthTokenLogoutHandler(AuthTokenService authTokenService, ObjectMapper mapper) {
    	return new AuthTokenLogoutHandler(authTokenService, mapper);
    }
    
    @Bean
    public JwtAuthorizationProvider buildJwtAuthorizationProvider(@Qualifier("jwtKey") WrapperKey jwtKey, ObjectMapper mapper,
            BuilderTokenStrategy builder, AuthTokenService authTokenService) {
    	return new JwtAuthorizationProvider(jwtKey, mapper, builder, authTokenService);
    }
}