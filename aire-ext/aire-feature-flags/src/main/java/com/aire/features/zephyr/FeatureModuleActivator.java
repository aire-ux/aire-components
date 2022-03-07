package com.aire.features.zephyr;

import com.aire.ux.Registration;
import com.aire.ux.UserInterface;
import com.aire.ux.ext.spring.SpringDelegatingInstantiator;
import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import io.zephyr.api.ServiceReference;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@Import(FeatureModuleConfiguration.class)
public class FeatureModuleActivator implements ModuleActivator {

  private Registration registration;
  private ConfigurableApplicationContext applicationContext;

  @Override
  public void start(ModuleContext context) throws Exception {
    context.getReferences(SpringDelegatingInstantiator.class)
        .stream().findFirst().ifPresent(instantiator -> initializeContext(context, instantiator));
  }


  @Override
  public void stop(ModuleContext context) throws Exception {
    if (applicationContext != null) {
      applicationContext.close();
    }
    if (registration != null) {
      registration.close();
    }
  }

  private void initializeContext(
      ModuleContext moduleContext, ServiceReference<SpringDelegatingInstantiator> reference) {
    registration = reference.getDefinition().get()
        .performWithParent(parent -> applicationContext = new SpringApplicationBuilder()
            .headless(true)
            .web(WebApplicationType.NONE)
            .initializers(new FeatureModuleInitializer(moduleContext))
            .sources(FeatureModuleConfiguration.class)
            .parent((ConfigurableApplicationContext) parent)
            .run()
        );
  }


  static class FeatureModuleInitializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    final UserInterface userInterface;

    public FeatureModuleInitializer(ModuleContext context) {
      userInterface = context.getReferences(UserInterface.class).stream().findFirst()
          .map(t -> t.getDefinition().get()).orElseThrow(
              () -> new IllegalArgumentException(
                  "No UserInterface services registered.  Either the dependency graph is incorrect or there is no supplier of such a bean"));
    }


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      applicationContext.getBeanFactory()
          .registerSingleton("aire.userinterface.instance", userInterface);
    }
  }
}
