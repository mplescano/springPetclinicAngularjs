package org.springframework.samples.petclinic.config.root;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.ResourceUtils;

@Configuration
public class InitDataSourceConfig {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ResourcePatternResolver resourceLoader;
	
	@PostConstruct
	public void init() throws IOException {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		
		Resource[] schemas = resourceLoader.getResources(env.getProperty("spring.datasource.schema"));
		for (Resource schema : schemas) {
			databasePopulator.addScript(schema);
		}
		
		Resource[] datas = resourceLoader.getResources(env.getProperty("spring.datasource.data"));
		for (Resource data : datas) {
			databasePopulator.addScript(data);
		}
		DatabasePopulatorUtils.execute(databasePopulator, dataSource);
	}

}