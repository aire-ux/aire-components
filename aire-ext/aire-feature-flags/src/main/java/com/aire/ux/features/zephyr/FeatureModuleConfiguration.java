package com.aire.ux.features.zephyr;

import com.aire.ux.features.FeatureManager;
import com.aire.ux.features.InMemoryFeatureManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureModuleConfiguration {

  @Bean
  public FeatureManager featureManager() {
    return new InMemoryFeatureManager();
  }

}
