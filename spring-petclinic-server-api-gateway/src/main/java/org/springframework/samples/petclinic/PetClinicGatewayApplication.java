package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
//could it be commented because el gateway is not gonna have many instances of itself??
//We need to have discovery client enabled, because gateway-service integrates with 
//Eureka in order to be able to perform routing to the downstream services.
@EnableDiscoveryClient
public class PetClinicGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicGatewayApplication.class, args);
    }
}
