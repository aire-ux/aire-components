package com.aire.ux;

import com.aire.ux.core.decorators.ComponentDecorator;
import java.io.InputStream;
import java.util.List;

public interface Theme {

  /** @return the ID for this theme */
  String getId();

  /**
   * @param path the path of the resource to open
   * @return an inputstream
   * @throws java.util.NoSuchElementException if a resource doesn't exist at that path
   */
  InputStream openResource(String path);

  /** @return a list of theme resources exported by this theme */
  List<ThemeResource> getThemeResources();

  /** @return the component-decorator for this theme */
  ComponentDecorator getComponentDecorator();
}
