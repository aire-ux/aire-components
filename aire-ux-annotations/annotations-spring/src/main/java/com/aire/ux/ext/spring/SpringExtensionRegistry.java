package com.aire.ux.ext.spring;

import com.aire.ux.Extension;
import com.aire.ux.ExtensionRegistration;
import com.aire.ux.PartialSelection;
import com.aire.ux.RouteDefinition;
import com.aire.ux.UserInterface;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.internal.AbstractRouteRegistry;
import com.vaadin.flow.server.VaadinContext;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.concurrent.ThreadSafe;
import lombok.NonNull;
import lombok.val;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@ThreadSafe
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SpringExtensionRegistry extends AbstractRouteRegistry
    implements ExtensionRegistry, ApplicationContextAware {

  final Object lock = new Object();
  public static final int CACHE_SIZE = 100;
  private final AccessQueue accessQueue;
  private final Supplier<VaadinContext> vaadinContext;
  private final Map<Class<?>, DefaultExtensionRegistration<?>> extensionCache;
  private final Map<PartialSelection<?>, DefaultExtensionRegistration<?>> extensions;

  private ApplicationContext context;
  private UserInterface userInterface;

  public SpringExtensionRegistry(AccessQueue accessQueue, Supplier<VaadinContext> context) {
    this.accessQueue = accessQueue;
    this.extensions = new HashMap<>();
    this.vaadinContext = context;
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
              });
      extensions.put(select, registration);
      return registration;
    }
  }

  @Override
  public <T extends HasElement> ExtensionRegistration register(RouteDefinition routeDefinition) {
    synchronized (lock) {
      val configuration = locate(routeDefinition);
      accessQueue.enqueue(
          () -> {
            val type = routeDefinition.getComponent();
            if (!configuration.isRouteRegistered(type)) {
              configuration.setAnnotatedRoute(type);
            }
          });
      return () -> {
        configuration.removeRoute(routeDefinition.getComponent());
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
                selection.select(component, getUserInterface()).ifPresent(ext::decorate);
              });
    }
  }

  @Override
  public int getExtensionCount() {
    synchronized (extensions) {
      return extensions.size();
    }
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
}
