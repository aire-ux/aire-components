package com.aire.ux.annotations.scenario1;

import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringComponentInclusionManager;
import com.aire.ux.ext.spring.SpringExtensionRegistry;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;
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
    return new SpringExtensionRegistry(
        new AccessQueue() {
          @Override
          public void enqueue(Command command) {
            UI.getCurrent().access(command);
          }

          @Override
          public void drain(VaadinSession session) {}
        },
        () -> null,
        new SpringComponentInclusionManager());
    //    return new SpringExtensionRegistry(command -> UI.getCurrent().access(command));
  }
}
