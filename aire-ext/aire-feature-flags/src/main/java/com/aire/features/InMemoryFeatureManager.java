package com.aire.features;

import com.aire.ux.ExtensionDefinition;
import com.aire.ux.Registration;
import com.aire.ux.UserInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InMemoryFeatureManager implements FeatureManager {

  private final UserInterface ui;
  private final List<FeatureDescriptor> descriptors;

  public InMemoryFeatureManager(UserInterface ui) {
    this.ui = ui;
    this.descriptors = new ArrayList<>();
  }

  @Override
  public List<FeatureDescriptor> getDescriptors() {
    return descriptors;
  }

  @Override
  public Registration registerFeature(FeatureDescriptor featureDescriptor) {
    descriptors.add(featureDescriptor);
    return () -> descriptors.remove(featureDescriptor);
  }

  @Override
  public Optional<FeatureDescriptor> featureFor(ExtensionDefinition<?> extension) {
    return descriptors.stream()
        .filter(descriptor -> Objects.equals(descriptor.getPath(), extension.getPath()))
        .findAny();
  }

  @Override
  public Optional<FeatureDescriptor> locate(String key) {
    return descriptors.stream()
        .filter(descriptor -> Objects.equals(descriptor.getKey(), key))
        .findAny();
  }

  @Override
  public boolean enable(String key) {
    return locate(key)
        .map(
            descriptor -> {
              descriptor.setEnabled(true, ui);
              return descriptor;
            })
        .isPresent();
  }

  @Override
  public boolean disable(String key) {
    return locate(key)
        .map(
            descriptor -> {
              descriptor.setEnabled(false, ui);
              return descriptor;
            })
        .isPresent();
  }
}
