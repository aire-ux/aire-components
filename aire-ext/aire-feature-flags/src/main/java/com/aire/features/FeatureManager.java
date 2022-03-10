package com.aire.features;

import com.aire.ux.ExtensionDefinition;
import com.aire.ux.Registration;
import java.util.List;
import java.util.Optional;

public interface FeatureManager {

  List<FeatureDescriptor> getDescriptors();

  Registration registerFeature(FeatureDescriptor featureDescriptor);

  Optional<FeatureDescriptor> featureFor(ExtensionDefinition<?> extension);

  Optional<FeatureDescriptor> locate(String key);

  boolean enable(String key);

  boolean disable(String key);
}
