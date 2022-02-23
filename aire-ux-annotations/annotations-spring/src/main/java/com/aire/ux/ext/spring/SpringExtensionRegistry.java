package com.aire.ux.ext.spring;

import com.aire.ux.Host;
import com.aire.ux.UIExtension;
import com.aire.ux.ext.ExtensionDefinition;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.ExtensionTree;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServlet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.val;
import org.springframework.aop.support.AopUtils;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SpringExtensionRegistry implements ExtensionRegistry {

  private final Map<String, ExtensionDefinition> extensionDefinitions;
  private final Map<Class<? extends HasElement>, ExtensionTree> componentPaths;

  public SpringExtensionRegistry() {
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
    RouteConfiguration.forApplicationScope().setAnnotatedRoute((Class) type);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> T instantiate(Class<T> type) {
    return VaadinServlet.getCurrent().getService().getInstantiator().getOrCreate(type);
  }

  @Override
  public Class<?> typeOf(HasElement type) {
    return AopUtils.getTargetClass(type);
  }
}
