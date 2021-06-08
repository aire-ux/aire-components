package com.aire.ux.spring.test.scenario2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Scenario2Configuration {

  @Bean
  public TestService testService() {
    return new TestService();
  }
}
