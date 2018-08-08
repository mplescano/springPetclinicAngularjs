package org.springframework.samples.petclinic.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.samples.petclinic.repository.support.ProjectableSpecificationExecutorImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
		"org.springframework.samples.petclinic.repository.regular" }, 
            repositoryBaseClass = ProjectableSpecificationExecutorImpl.class,
		        entityManagerFactoryRef = "regularEntityManager", 
		        transactionManagerRef = "regularTransactionManager")
public class JpaRegularConfig {

	@Bean("regularEntityManager")
	@Primary
	public LocalContainerEntityManagerFactoryBean regularEntityManager(
			EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource") DataSource dataSource) {
		return builder.dataSource(dataSource)
				.packages("org.springframework.samples.petclinic.model.regular")
				.persistenceUnit("regular").build();
	}

	@Bean("regularTransactionManager")
	public PlatformTransactionManager regularTransactionManager(
			@Qualifier("regularEntityManager") EntityManagerFactory regularEntityManagerFactory) {
		return new JpaTransactionManager(regularEntityManagerFactory);
	}
}
