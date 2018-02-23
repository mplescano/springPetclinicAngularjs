package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetClinicApplication /*extends SpringBootServletInitializer*/ {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }
    
/*	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PetClinicApplication.class);
	}*/
}

