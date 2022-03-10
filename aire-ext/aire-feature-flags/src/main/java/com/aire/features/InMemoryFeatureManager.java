package com.aire.features;

import com.aire.ux.ExtensionDefinition;
import com.aire.ux.Registration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InMemoryFeatureManager implements FeatureManager {

  private final List<FeatureDescriptor> descriptors;

  public InMemoryFeatureManager() {
    this.descriptors = new ArrayList<>();
  }

  public static InMemoryFeatureManager getInstance() {
    return Holder.instance;
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
              descriptor.setEnabled(true);
              return descriptor;
            })
        .isPresent();
  }

  @Override
  public boolean disable(String key) {
    return locate(key)
        .map(
            descriptor -> {
              descriptor.setEnabled(false);
              return descriptor;
            })
        .isPresent();
  }

  static final class Holder {

    static final InMemoryFeatureManager instance = new InMemoryFeatureManager();
  }
}
