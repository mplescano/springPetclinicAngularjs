package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.samples.petclinic.config.PetclinicProperties;

@SpringBootApplication
@EnableConfigurationProperties(PetclinicProperties.class)
public class PetClinicApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }
    
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PetClinicApplication.class);
	}
}

