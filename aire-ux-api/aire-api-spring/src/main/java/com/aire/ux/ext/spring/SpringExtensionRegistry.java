package com.aire.ux.ext.spring;

import static io.sunshower.lang.events.Events.create;

import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.Extension;
import com.aire.ux.ExtensionDefinition;
import com.aire.ux.ExtensionRegistration;
import com.aire.ux.PartialSelection;
import com.aire.ux.RouteDefinition;
import com.aire.ux.Selection;
import com.aire.ux.UserInterface;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.internal.AbstractRouteRegistry;
import com.vaadin.flow.server.VaadinContext;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.lang.events.EventSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.concurrent.ThreadSafe;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.val;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@ThreadSafe
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SpringExtensionRegistry extends AbstractRouteRegistry
    implements ExtensionRegistry, ApplicationContextAware, DisposableBean {

  public static final int CACHE_SIZE = 100;
  private final Object lock = new Object();
  @Delegate private final EventSource delegate;
  private final AccessQueue accessQueue;
  private final Supplier<VaadinContext> vaadinContext;
  private final Map<Class<?>, DefaultExtensionRegistration<?>> extensionCache;
  private final Map<PartialSelection<?>, DefaultExtensionRegistration<?>> extensions;
  private final ComponentInclusionManager inclusionManager;
  private ApplicationContext context;
  private UserInterface userInterface;

  public SpringExtensionRegistry(
      AccessQueue accessQueue,
      Supplier<VaadinContext> context,
      ComponentInclusionManager inclusionManager) {
    this.accessQueue = accessQueue;
    this.extensions = new HashMap<>();
    this.vaadinContext = context;
    this.inclusionManager = inclusionManager;
    this.delegate = new SpringExtensionRegistryEventSource();
    extensionCache =
        new LinkedHashMap<>() {
          @Override
          protected boolean removeEldestEntry(
              Entry<Class<?>, DefaultExtensionRegistration<?>> eldest) {
            return size() >= CACHE_SIZE;
          }
        };
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> T instantiate(Class<T> type) {
    synchronized (lock) {
      val factory = context.getAutowireCapableBeanFactory();
      return factory.createBean(type);
    }
  }

  @Override
  public Class<?> typeOf(Object type) {
    return AopUtils.getTargetClass(type);
  }

  @Override
  public <T extends HasElement> ExtensionRegistration register(
      PartialSelection<T> select, Extension<T> extension) {
    synchronized (extensions) {
      val registration =
          new DefaultExtensionRegistration<>(
              select,
              extension,
              () -> {
                extensions.remove(select);
                extensionCache.clear();
                dispatchEvent(Events.ExtensionUnregistered, create(extension));
              });
      extensions.put(select, registration);
      dispatchEvent(Events.ExtensionRegistered, create(extension));
      return registration;
    }
  }

  @Override
  public <T extends HasElement> ExtensionRegistration register(RouteDefinition routeDefinition) {
    synchronized (lock) {
      accessQueue.enqueue(
          () -> {
            val configuration = locate(routeDefinition);
            val type = routeDefinition.getComponent();
            if (!configuration.isRouteRegistered(type)) {
              configuration.setAnnotatedRoute(type);
            }
            dispatchEvent(Events.RouteRegistered, create(routeDefinition));
          });
      return () -> {
        accessQueue.enqueue(
            () -> {
              val cfg = locate(routeDefinition);
              cfg.removeRoute(routeDefinition.getComponent());
              dispatchEvent(Events.RouteUnregistered, create(routeDefinition));
            });
      };
    }
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean isRegistered(Class<?> type) {
    synchronized (lock) {
      return getExtension((Class) type).isPresent();
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends HasElement> Optional<Extension<T>> getExtension(Class<T> type) {
    synchronized (lock) {
      return resolve(type).map(ext -> (Extension<T>) ext.getExtension());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void decorate(Class<?> type, HasElement component) {
    synchronized (lock) {
      resolve(type)
          .ifPresent(
              ext -> {
                val selection = ext.getSelection();
                val opt = selection.select(component, getUserInterface(), ext.getExtension());
                opt.ifPresent(
                    extDef -> {
                      val definition = (ExtensionDefinition<?>) extDef;
                      if (inclusionManager.decide(definition)) {
                        ext.decorate(definition.getValue());
                      }
                    });
              });
    }
  }

  @Override
  public int getExtensionCount() {
    synchronized (extensions) {
      return extensions.size();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ExtensionDefinition<?>> getExtensions() {
    return extensions.entrySet().stream()
        .map(
            (kv) -> {
              val selection = kv.getKey();
              val registration = kv.getValue();
              return new PartialExtensionDefinition<>(
                  selection.getSegment(), registration.getExtension().getSegment());
            })
        .collect(Collectors.toList());
  }

  @Override
  public @NonNull ComponentInclusionManager getComponentInclusionManager() {
    return inclusionManager;
  }

  private UserInterface getUserInterface() {
    synchronized (lock) {
      if (userInterface != null) {
        return userInterface;
      }
      return (userInterface = context.getBean(UserInterface.class));
    }
  }

  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext)
      throws BeansException {
    this.context = applicationContext;
  }

  @SuppressWarnings("PMD.CloseResource")
  private Optional<DefaultExtensionRegistration> resolve(Class<?> type) {
    synchronized (lock) {
      DefaultExtensionRegistration result = null;
      synchronized (extensionCache) {
        if (extensionCache.containsKey(type)) {
          result = extensionCache.get(type);
        }
      }

      synchronized (extensions) {
        for (val ext : extensions.entrySet()) {
          if (ext.getKey().isHostedBy(type)) {
            result = ext.getValue();
            extensionCache.put(type, result);
          }
        }
      }
      return Optional.ofNullable(result);
    }
  }

  @Override
  public VaadinContext getContext() {
    return vaadinContext.get();
  }

  @NonNull
  @SuppressWarnings("PMD.MissingBreakInSwitch")
  private RouteConfiguration locate(RouteDefinition routeDefinition) {

    final RouteConfiguration configuration;
    switch (routeDefinition.getMode()) {
      case Global:
        configuration = RouteConfiguration.forApplicationScope();
        break;
      case Session:
        configuration = RouteConfiguration.forSessionScope();
        break;
      case Aire:
      default:
        configuration = RouteConfiguration.forRegistry(this);
    }
    return configuration;
  }

  @Override
  public void close() throws Exception {
    // todo remove listeners
  }

  @Override
  public void destroy() throws Exception {
    close();
  }

  private static class SpringExtensionRegistryEventSource extends AbstractEventSource {}

  private static class PartialExtensionDefinition<T> implements ExtensionDefinition<T> {

    private final String path;

    public PartialExtensionDefinition(String path, String segment) {
      this.path = path + segment;
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public T getValue() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Extension<T> getExtension() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Selection<T> getSelection() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<T> getType() {
      throw new UnsupportedOperationException();
    }
  }
}
