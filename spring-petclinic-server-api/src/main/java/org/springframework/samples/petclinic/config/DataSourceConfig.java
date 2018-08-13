package org.springframework.samples.petclinic.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    @Primary
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Bean("dataSource")
    @Primary
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties("spring.security.datasource")
    public DataSourceProperties dataSourceSecurityProperties() {
        return new DataSourceProperties();
    }

    @Bean("dataSourceSecurity")
    public DataSource dataSourceSecurity() {
        return dataSourceSecurityProperties().initializeDataSourceBuilder().build();
    }
}