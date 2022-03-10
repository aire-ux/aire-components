package com.aire.ux.features;

import com.aire.features.FeatureManager;
import com.aire.features.InMemoryFeatureManager;
import com.aire.ux.UserInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

  @Bean
  public FeatureManager featureManager(UserInterface userInterface) {
    return new InMemoryFeatureManager(userInterface);
  }
}
