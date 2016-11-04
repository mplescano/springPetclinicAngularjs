package org.springframework.samples.petclinic.config.root;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class InitDataSourceConfig {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private DataSource dataSource;
	
	@PostConstruct
	public void init() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource(env.getProperty("spring.datasource.schema")));
		databasePopulator.addScript(new ClassPathResource(env.getProperty("spring.datasource.data")));
		DatabasePopulatorUtils.execute(databasePopulator, dataSource);
	}

}