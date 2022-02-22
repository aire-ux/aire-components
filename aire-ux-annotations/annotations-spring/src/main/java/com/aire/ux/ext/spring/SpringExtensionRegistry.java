package com.aire.ux.ext.spring;

import com.aire.ux.ext.ExtensionDefinition;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.HasElement;
import java.util.List;
import java.util.WeakHashMap;

public class SpringExtensionRegistry implements ExtensionRegistry {

  private final WeakHashMap<Class<?>, ExtensionDefinition> definitions;

  public SpringExtensionRegistry() {
    definitions = new WeakHashMap<>();
  }

  @Override
  public void register(ExtensionDefinition definition) {
    synchronized (definitions) {
      if (!definitions.containsKey(definition.getType())) {
        definitions.put(definition.getType(), definition);
      }
    }
  }

  @Override
  public boolean isRegistered(ExtensionDefinition definition) {
    return false;
  }

  @Override
  public ExtensionDefinition unregister(ExtensionDefinition definition) {
    return null;
  }

  @Override
  public List<ExtensionDefinition> getDefinitions() {
    return List.copyOf(definitions.values());
  }

  @Override
  public boolean isRegistered(Class<? extends HasElement> value) {
    synchronized (definitions) {
      return definitions.containsKey(value);
    }
  }
}
