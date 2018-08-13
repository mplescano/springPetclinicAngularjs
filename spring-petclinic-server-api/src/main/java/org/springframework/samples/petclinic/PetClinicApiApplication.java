package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PetClinicApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApiApplication.class, args);
    }
}
