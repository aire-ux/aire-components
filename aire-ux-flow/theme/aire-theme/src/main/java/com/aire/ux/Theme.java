package com.aire.ux;

import com.vaadin.flow.component.HasElement;
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

  /**
   * apply this theme to the component. This method may do nothing for some components (or many)
   *
   * @param value the value to apply this theme to
   * @param <T> the type of the component
   */
  <T extends HasElement> void apply(T value);
}
