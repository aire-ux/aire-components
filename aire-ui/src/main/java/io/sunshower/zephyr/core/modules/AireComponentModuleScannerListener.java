package io.sunshower.zephyr.core.modules;

import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.shared.Registration;
import io.zephyr.kernel.Coordinate;
import io.zephyr.kernel.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AireComponentModuleScannerListener {

  private final ThreadPoolTaskExecutor executor;
  private final ExtensionRegistry extensionRegistry;
  private final Map<Coordinate, ComponentContext> contexts;

  @Inject
  public AireComponentModuleScannerListener(
      final ThreadPoolTaskExecutor executor, final ExtensionRegistry extensionRegistry) {
    this.executor = executor;
    this.contexts = new HashMap<>();
    this.extensionRegistry = extensionRegistry;
  }

  @EventListener
  public void onModuleStarted(ModuleStartedEvent event) {
    CompletableFuture.supplyAsync(new AireComponentScanner(event.getSource()), executor)
        .thenAccept(types -> registerComponents(event.getSource(), types));
  }

  @EventListener
  public void onModuleStopped(ModuleStoppedEvent event) {

    synchronized (contexts) {
      val module = event.getSource();
      val coordinate = module.getCoordinate();
      val context = contexts.remove(coordinate);

      if (context != null) {
        val registrations = context.componentRegistrations;
        val iterator = registrations.iterator();
        synchronized (registrations) {
          while (iterator.hasNext()) {
            val next = iterator.next();
            next.remove();
            iterator.remove();
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void registerComponents(Module source, Set<Class<?>> types) {
    synchronized (contexts) {
      val coordinate = source.getCoordinate();
      if (contexts.containsKey(coordinate)) {
        throw new IllegalStateException(
            "Error: Attempting to re-register module '%s' somehow"
                .formatted(coordinate.toCanonicalForm()));
      }
      val context = new ComponentContext();
      for (val type : types) {
        if (!HasElement.class.isAssignableFrom(type)) {
          throw new IllegalStateException(
              "Error: type '%s' from module '%s' is not an instance of a component"
                  .formatted(type, coordinate));
        }
        //        extensionRegistry.defineExtension((Class<? extends HasElement>) type);
        //        context.addRegistration(
        //            () -> {
        //              extensionRegistry.removeExtension((Class<? extends HasElement>) type);
        //            });
      }
      contexts.put(coordinate, context);
    }
  }

  static final class ComponentContext {

    private final List<Registration> componentRegistrations;

    ComponentContext() {
      componentRegistrations = new ArrayList<>();
    }

    public void addRegistration(Registration o) {
      componentRegistrations.add(o);
    }
  }
}
