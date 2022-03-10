package com.aire.features;

import com.aire.ux.ExtensionDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class FeatureDescriptor {

  @Getter private final String key;
  @Getter private final String name;
  @Getter private final String description;
  @Getter private final String path;
  @Getter private final List<String> tags;

  @Getter @Setter private boolean enabled;

  public FeatureDescriptor(
      @NonNull final String key,
      @NonNull final String name,
      @NonNull final String path,
      @NonNull final String description) {
    this.key = key;
    this.path = path;
    this.name = name;
    this.description = description;
    this.tags = new ArrayList<>();
  }

  public void enable() {
    this.enabled = true;
  }

  public void disable() {
    this.enabled = false;
  }

  public boolean matches(ExtensionDefinition<?> definition) {
    return path.equals(definition.getPath());
  }

  public void addTag(@NonNull String tag) {
    tags.add(tag);
  }

  public void removeTag(@NonNull String tag) {
    tags.remove(tag);
  }

  public List<String> getTags() {
    return tags;
  }
}
