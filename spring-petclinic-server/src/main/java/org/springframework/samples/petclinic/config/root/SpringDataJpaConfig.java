package org.springframework.samples.petclinic.config.root;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("org.springframework.samples.petclinic.repository")
public class SpringDataJpaConfig {

}