package com.aire.ux.ext;


import com.vaadin.flow.component.HasElement;
import java.util.Optional;

public interface ExtensionRegistry {

  int getHostCount();

  Optional<ExtensionTree> defineHost(Class<? extends HasElement> host);

  boolean defineExtension(Class<? extends HasElement> value);

  int getExtensionCount();

  void bind(ExtensionTree r, HasElement component);

  Class<?> typeOf(HasElement type);
}
