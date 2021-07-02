package com.aire.ux;

import com.aire.ux.ThemeResource.Type;
import com.vaadin.flow.component.HasElement;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

  default List<ThemeResource> getThemeResources(Type type) {
    return getThemeResources().stream()
        .filter(t -> Objects.equals(type, t.getType()))
        .collect(Collectors.toList());
  }

  /**
   * @param resource the resource to lookup
   * @return the resource, or null if no resource with the name is found
   */
  @Nullable
  default ThemeResource getThemeResource(@Nonnull String resource) {
    return getThemeResources().stream()
        .filter(t -> t.getName().equals(resource))
        .findAny()
        .orElse(null);
  }
}
