package com.aire.ux.ext;

import com.vaadin.flow.component.HasElement;
import java.util.List;

public interface ExtensionRegistry {

  void register(ExtensionDefinition definition);

  boolean isRegistered(ExtensionDefinition definition);

  ExtensionDefinition unregister(ExtensionDefinition definition);

  List<ExtensionDefinition> getDefinitions();

  boolean isRegistered(Class<? extends HasElement> value);
}
