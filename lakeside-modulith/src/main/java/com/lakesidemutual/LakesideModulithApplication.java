package com.lakesidemutual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
@SpringBootApplication
@EntityScan(basePackages = {
        "com.lakesidemutual.customercore.domain",                       // keep Customer Core entities
        "com.lakesidemutual.customerselfservice.domain.identityaccess", // keep self-service login entities
        "com.lakesidemutual.customerselfservice.domain.insurancequoterequest" // keep self-service quote entities
})
public class LakesideModulithApplication {

  public static void main(String[] args) {
    SpringApplication.run(LakesideModulithApplication.class, args);
  }
}
