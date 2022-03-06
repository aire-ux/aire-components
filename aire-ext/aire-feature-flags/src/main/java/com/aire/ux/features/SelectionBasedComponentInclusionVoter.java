package com.aire.ux.features;

import com.aire.ux.ComponentInclusionVoter;
import com.aire.ux.ExtensionDefinition;
import lombok.NonNull;
import lombok.val;

public class SelectionBasedComponentInclusionVoter implements ComponentInclusionVoter {

  private final FeatureManager featureManager;

  public SelectionBasedComponentInclusionVoter(@NonNull final FeatureManager featureManager) {
    this.featureManager = featureManager;
  }

  @Override
  public boolean decide(ExtensionDefinition<?> extension) {
    val feature = featureManager.featureFor(extension);
    return feature.map(FeatureDescriptor::isEnabled).orElse(true);
  }
}
