package com.lakesidemutual;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class MigrationRestTemplateConfig {

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
