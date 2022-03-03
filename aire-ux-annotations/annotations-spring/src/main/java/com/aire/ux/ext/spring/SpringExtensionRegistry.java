package com.aire.ux.ext.spring;

import com.aire.ux.Host;
import com.aire.ux.UIExtension;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionDefinition;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.ExtensionTree;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.val;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SpringExtensionRegistry implements ExtensionRegistry, ApplicationContextAware {

  private final AccessQueue accessQueue;
  private final Map<String, ExtensionDefinition> extensionDefinitions;
  private final Map<Class<? extends HasElement>, ExtensionTree> componentPaths;
  private ApplicationContext context;

  public SpringExtensionRegistry(AccessQueue accessQueue) {
    this.accessQueue = accessQueue;
    componentPaths = new HashMap<>();
    extensionDefinitions = new HashMap<>();
  }

  @Override
  public int getHostCount() {
    return componentPaths.size();
  }

  @Override
  public Optional<ExtensionTree> defineHost(Class<? extends HasElement> host) {
    if (host.isAnnotationPresent(Host.class)) {
      return Optional.of(componentPaths.computeIfAbsent(host, k -> new ExtensionTree(k, this)));
    }
    return Optional.empty();
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean defineExtension(Class<? extends HasElement> value) {
    synchronized (extensionDefinitions) {
      val extDefinition = value.getAnnotation(UIExtension.class);
      if (extDefinition == null) {
        return false;
      }
      val control = extDefinition.control();
      val controlTarget = control.target();
      val definition =
          new ExtensionDefinition(
              controlTarget, (Supplier<Component>) instantiate(control.factory()), value);
      extensionDefinitions.put(controlTarget, definition);

      return true;
    }
  }

  @Override
  public boolean removeExtension(Class<? extends HasElement> value) {
    synchronized (extensionDefinitions) {
      val extDefinition = value.getAnnotation(UIExtension.class);
      if (extDefinition == null) {
        return false;
      }
      val control = extDefinition.control();
      val controlTarget = control.target();
      if (value.isAnnotationPresent(Route.class)) {
        unregisterRoute(value);
      }
      return extensionDefinitions.remove(controlTarget) != null;
    }
  }

  @Override
  public int getExtensionCount() {
    synchronized (extensionDefinitions) {
      return extensionDefinitions.size();
    }
  }

  @Override
  public void bind(ExtensionTree tree, HasElement component) {
    synchronized (extensionDefinitions) {
      for (val definition : extensionDefinitions.values()) {
        tree.componentAt(definition.getPath(), component)
            .ifPresent(
                c -> {
                  insert(c, definition);
                });
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void insert(HasElement c, ExtensionDefinition definition) {
    if (c instanceof Component) {
      if (definition.getType().isAnnotationPresent(Route.class)) {
        registerRoute(definition.getType());
      }
      c.getElement().appendChild(definition.create().getElement());
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void registerRoute(Class<? extends HasElement> type) {
    accessQueue.enqueue(
        () -> {
          val t = (Class<? extends Component>) type;
          val scope = RouteConfiguration.forApplicationScope();
          if (!scope.isRouteRegistered(t)) {
            scope.setAnnotatedRoute(t);
          }
        });
  }

  @SuppressWarnings("unchecked")
  private void unregisterRoute(Class<? extends HasElement> type) {
    accessQueue.enqueue(
        () -> {
          RouteConfiguration.forApplicationScope().removeRoute((Class<? extends Component>) type);
        });
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> T instantiate(Class<T> type) {
    val factory = context.getAutowireCapableBeanFactory();
    return factory.createBean(type);
  }

  @Override
  public Class<?> typeOf(Object type) {
    return AopUtils.getTargetClass(type);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }
}
