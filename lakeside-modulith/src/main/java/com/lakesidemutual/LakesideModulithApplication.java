package com.lakesidemutual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(exclude = {
    net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class
})
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class LakesideModulithApplication {

  public static void main(String[] args) {
    SpringApplication.run(LakesideModulithApplication.class, args);
  }
}
