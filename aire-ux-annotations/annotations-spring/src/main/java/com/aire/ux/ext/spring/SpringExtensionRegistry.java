package com.aire.ux.ext.spring;

import com.aire.ux.Host;
import com.aire.ux.UIExtension;
import com.aire.ux.ext.ExtensionDefinition;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.ExtensionTree;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.server.VaadinServlet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.val;
import org.springframework.aop.support.AopUtils;

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
  public boolean defineExtension(Class<? extends HasElement> value) {
    val extDefinition = value.getAnnotation(UIExtension.class);
    if (extDefinition == null) {
      return false;
    }
    val definition = new ExtensionDefinition(extDefinition.target(), value);
    extensionDefinitions.put(extDefinition.target(), definition);
    return true;
  }

  @Override
  public int getExtensionCount() {
    return extensionDefinitions.size();
  }

  @Override
  public void bind(ExtensionTree tree, HasElement component) {
    for (val definition : extensionDefinitions.values()) {
      tree.componentAt(definition.getPath(), component).ifPresent(c -> {
        insert(c, definition);
      });
    }
  }

  @SuppressWarnings("unchecked")
  private void insert(HasElement c, ExtensionDefinition definition) {
    if (c instanceof Component) {
      val component = (Component) c;
      val child = instantiate(definition.getType());
      c.getElement().appendChild(child.getElement());
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private HasElement instantiate(Class<? extends HasElement> type) {
    return (HasElement) VaadinServlet
        .getCurrent()
        .getService()
        .getInstantiator()
        .createComponent((Class) type);
  }

  @Override
  public Class<?> typeOf(HasElement type) {
    return AopUtils.getTargetClass(type);
  }
}
