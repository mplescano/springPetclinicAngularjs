package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.samples.petclinic.repository.support.ProjectableSpecificationExecutorImpl;

@Configuration
@EnableJpaRepositories(basePackages = {"org.springframework.samples.petclinic.repository"},
		repositoryBaseClass = ProjectableSpecificationExecutorImpl.class)
public class SpringDataJpaConfig {

}
