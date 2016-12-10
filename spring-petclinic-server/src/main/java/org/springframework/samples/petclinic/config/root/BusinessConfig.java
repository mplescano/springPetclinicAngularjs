package org.springframework.samples.petclinic.config.root;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.samples.petclinic.config.PetclinicProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("org.springframework.samples.petclinic.service")
// Configurer that replaces ${...} placeholders with values from a properties file
// (in this case, JDBC-related settings for the JPA EntityManager definition below)
@EnableTransactionManagement
@Import({PetclinicProperties.class, DataSourceConfig.class, 
	InitDataSourceConfig.class, SharedJpaConfig.class, SpringDataJpaConfig.class})
public class BusinessConfig {
		

}