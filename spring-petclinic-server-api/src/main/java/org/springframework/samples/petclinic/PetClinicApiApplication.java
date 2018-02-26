package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetClinicApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApiApplication.class, args);
    }
    
/*	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PetClinicApplication.class);
	}*/
}

