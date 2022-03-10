package com.aire.ux.ext.spring;

import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.annotations.TestAccessQueue;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.server.VaadinService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {


  @Bean
  public ComponentInclusionManager componentInclusionManager() {
    return new SpringComponentInclusionManager();
  }

  @Bean
  public AccessQueue accessQueue() {
    return new TestAccessQueue();
  }

  @Bean
  public ExtensionRegistry extensionRegistry(AccessQueue queue,
      ComponentInclusionManager manager) {
    return new SpringExtensionRegistry(queue, () -> VaadinService.getCurrent().getContext(),
        manager);
  }

}
