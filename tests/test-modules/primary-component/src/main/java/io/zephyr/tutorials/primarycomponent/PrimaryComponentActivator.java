package io.zephyr.tutorials.primarycomponent;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.WebApplicationType;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.ConfigurableApplicationContext;

// @SpringBootApplication
public class PrimaryComponentActivator implements ModuleActivator {

  //  private ConfigurableApplicationContext context;

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    System.out.println("Primary Component Running");
    //    System.out.println("Module 1 activated!");
    //    SpringApplication app = new SpringApplication(PrimaryComponentActivator.class);
    //    app.setWebApplicationType(WebApplicationType.NONE);
    //    this.context = app.run();
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    System.out.println("Primary Component Stopped");
    //    context.close();
    //    System.out.println("Module 1 deactivated!");
  }
}
