package com.aire.ux.annotations.scenario1;

import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringExtensionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan(basePackageClasses = FrontPage.class)
public class Scenario1 {
  @Bean
  @Scope("singleton")
  public static ExtensionRegistry extensionRegistry() {
    return new SpringExtensionRegistry();
  }
}
