package io.sunshower.zephyr.configuration;

import com.aire.ux.Aire;
import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.DefaultUserInterface;
import com.aire.ux.UserInterface;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.actions.DefaultActionManager;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringComponentInclusionManager;
import com.aire.ux.ext.spring.SpringExtensionRegistry;
import com.vaadin.flow.server.VaadinService;
import io.zephyr.api.ServiceRegistration;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.FactoryServiceDefinition;
import io.zephyr.kernel.core.Kernel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class ZephyrCoreConfiguration
    implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

  private ServiceRegistration<UserInterface> result;

  @Bean
  public static UserInterface userInterface(
      ExtensionRegistry extensionRegistry, AccessQueue accessQueue, ActionManager actionManager) {
    return Aire.setUserInterface(
        new DefaultUserInterface(extensionRegistry, accessQueue, actionManager));
  }

  @Bean
  public static ActionManager actionManager() {
    return new DefaultActionManager();
  }

  @Bean(name = "threadPoolTaskExecutor")
  public static ThreadPoolTaskExecutor executor() {
    val executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(8);
    return executor;
  }

  @Bean
  public ComponentInclusionManager componentInclusionManager() {
    return new SpringComponentInclusionManager();
  }

  @Bean
  public ExtensionRegistry extensionRegistry(AccessQueue queue, ComponentInclusionManager manager) {
    return new SpringExtensionRegistry(
        queue, () -> VaadinService.getCurrent().getContext(), manager);
  }

  @Bean(name = "applicationEventMulticaster")
  public ApplicationEventMulticaster applicationEventMulticaster(ThreadPoolTaskExecutor executor) {
    val result = new SimpleApplicationEventMulticaster();
    result.setTaskExecutor(executor);
    return result;
  }

  private void registerServices(
      Module module, ConfigurableApplicationContext context, Kernel kernel) {
    log.info("Registering UserInterface service");
    result =
        kernel
            .getServiceRegistry()
            .register(
                module,
                new FactoryServiceDefinition<>(
                    UserInterface.class,
                    "aire:user-interface",
                    () -> context.getBean(UserInterface.class)));
    log.info("Successfully registered UserInterface service");
  }

  @Override
  public void destroy() throws Exception {
    if (result != null) {
      result.dispose();
    }
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    val context = event.getApplicationContext();
    val kernel = context.getBean(Kernel.class);
    val module = context.getBean(Module.class);
    registerServices(module, context, kernel);
  }
}
