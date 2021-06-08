package com.aire.ux.spring.test.scenario1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Scenario1Configuration {

  @Bean
  public TestService testService() {
    return new TestService();
  }
}
