package com.aire.ux.ext.spring;

import static io.sunshower.lang.events.Events.create;

import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.Extension;
import com.aire.ux.ExtensionDefinition;
import com.aire.ux.ExtensionRegistration;
import com.aire.ux.PartialSelection;
import com.aire.ux.RouteDefinition;
import com.aire.ux.RouteExtensionDefinition;
import com.aire.ux.Selection;
import com.aire.ux.UserInterface;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.internal.AbstractRouteRegistry;
import com.vaadin.flow.server.VaadinContext;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.lang.events.EventSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private final Map<Class<?>, Set<RouteExtensionDefinition<?>>> routeExtensions;
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
    this.routeExtensions = new HashMap<>();
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
  public List<RouteDefinition> getRouteDefinitions() {
    return routeExtensions.entrySet().stream()
        .flatMap(t -> t.getValue().stream())
        .map(RouteExtensionDefinition::getRouteDefinition)
        .collect(Collectors.toList());
  }

  @Override
  public boolean unregister(RouteDefinition routeDefinition) {
    synchronized (routeExtensions) {
      val result = routeExtensions.get(routeDefinition.getComponent());
      boolean allClosed = true;
      if (result != null) {
        for (val r : result) {
          val finalizer = r.getFinalizer();
          if (finalizer != null) {
            finalizer.close();
          } else {
            allClosed = false;
          }
        }
      }
      return allClosed;
    }
  }

  @Override
  public List<RouteDefinition> getRouteDefinitionsFor(Class<? extends Component> component) {
    return Optional.ofNullable(routeExtensions.get(component))
        .map(
            t ->
                t.stream()
                    .map(RouteExtensionDefinition::getRouteDefinition)
                    .collect(Collectors.toList()))
        .stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  public ExtensionRegistration register(RouteDefinition routeDefinition) {
    synchronized (lock) {
      val definition = extensionDefinitionFor(routeDefinition);
      accessQueue.enqueue(
          () -> {
            val configurations = locate(routeDefinition);
            for (val configuration : configurations) {
              val type = routeDefinition.getComponent();
              if (!configuration.isRouteRegistered(type)) {
                configuration.setAnnotatedRoute(type);
              }
              routeExtensions
                  .computeIfAbsent(routeDefinition.getComponent(), k -> new HashSet<>())
                  .add(definition);
              dispatchEvent(Events.RouteRegistered, create(routeDefinition));
            }
          });
      ExtensionRegistration finalizer =
          () ->
              accessQueue.enqueue(
                  () -> {
                    val configurations = locate(routeDefinition);
                    for (val configuration : configurations) {
                      configuration.removeRoute(routeDefinition.getComponent());
                      routeExtensions.remove(routeDefinition.getComponent());
                      dispatchEvent(Events.RouteUnregistered, create(routeDefinition));
                    }
                  });
      definition.setFinalizer(finalizer);
      return finalizer;
    }
  }

  private RouteExtensionDefinition<?> extensionDefinitionFor(RouteDefinition routeDefinition) {
    return new RouteExtensionDefinition<>(routeDefinition);
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
      return extensions.size() + routeExtensions.size();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ExtensionDefinition<?>> getExtensions() {
    return Stream.concat(
            extensions.entrySet().stream()
                .map(
                    (kv) -> {
                      val selection = kv.getKey();
                      val registration = kv.getValue();
                      return new PartialExtensionDefinition<>(
                          selection.getSegment(), registration.getExtension().getSegment());
                    }),
            routeExtensions.values().stream().flatMap(Collection::stream))
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
  private List<RouteConfiguration> locate(RouteDefinition routeDefinition) {

    val results = new ArrayList<RouteConfiguration>();

    for (val scope : routeDefinition.getScopes()) {
      switch (scope) {
        case Global:
          results.add(RouteConfiguration.forApplicationScope());
          break;
        case Session:
          results.add(RouteConfiguration.forSessionScope());
          break;
        case Aire:
        default:
          results.add(RouteConfiguration.forRegistry(this));
      }
    }
    return results;
  }

  @Override
  public void close() throws Exception {
    extensions.clear();
    extensionCache.clear();
    routeExtensions.clear();
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
