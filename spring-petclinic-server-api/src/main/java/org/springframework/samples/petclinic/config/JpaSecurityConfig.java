package org.springframework.samples.petclinic.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.samples.petclinic.repository.support.ProjectableSpecificationExecutorImpl;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(basePackages = {"org.springframework.samples.petclinic.repository.security"},
		repositoryBaseClass = ProjectableSpecificationExecutorImpl.class)
public class JpaSecurityConfig {

	@Bean("securityEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean securityEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource") DataSource dataSource) {
		return builder.dataSource(dataSource)
				.packages("org.springframework.samples.petclinic.model.security")
				.persistenceUnit("security").build();
	}

	@Bean(name = "securityTransactionManager")
	public PlatformTransactionManager securityTransactionManager(
			@Qualifier("securityEntityManagerFactory") EntityManagerFactory securityEntityManagerFactory) {
		return new JpaTransactionManager(securityEntityManagerFactory);
	}
}
