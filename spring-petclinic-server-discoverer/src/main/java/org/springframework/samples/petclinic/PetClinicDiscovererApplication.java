package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class PetClinicDiscovererApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicDiscovererApplication.class, args);
    }
}
