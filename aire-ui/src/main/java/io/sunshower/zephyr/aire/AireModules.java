package io.sunshower.zephyr.aire;

import com.aire.ux.Registration;
import com.aire.ux.ext.spring.SpringDelegatingInstantiator;
import io.zephyr.api.ServiceReference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.val;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;

public final class AireModules {

  public static HierarchicalContextRegistration initializeContext(
      Collection<Class<?>> configurationTypes,
      ServiceReference<SpringDelegatingInstantiator> reference,
      ApplicationContextInitializer<?>... initializers) {
    val applicationContext = new AtomicReference<ConfigurableApplicationContext>();
    val parentContextRegistration =
        reference
            .getDefinition()
            .get()
            .performWithParent(
                parent -> {
                  applicationContext.set(
                      new SpringApplicationBuilder()
                          .headless(true)
                          .web(WebApplicationType.NONE)
                          .initializers(initializers)
                          .sources(configurationTypes.toArray(new Class<?>[0]))
                          .parent((ConfigurableApplicationContext) parent)
                          .run());
                  return applicationContext.get();
                });
    return new HierarchicalContextRegistration(applicationContext.get(), parentContextRegistration);
  }

  @Getter
  public static class HierarchicalContextRegistration implements Registration {

    final Registration parentRegistration;
    final ConfigurableApplicationContext childContext;

    public HierarchicalContextRegistration(
        ConfigurableApplicationContext childContext, Registration parentRegistration) {
      this.childContext = childContext;
      this.parentRegistration = parentRegistration;
    }

    @Override
    public void remove() {
      if (childContext != null) {
        childContext.stop();
      }

      if (parentRegistration != null) {
        if (parentRegistration instanceof Lifecycle lifecycle) {
          lifecycle.stop();
        } else {
          parentRegistration.remove();
        }
      }
    }
  }
}
