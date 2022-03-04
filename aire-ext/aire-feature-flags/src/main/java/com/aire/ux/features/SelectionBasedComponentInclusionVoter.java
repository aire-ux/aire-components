package com.aire.ux.features;

import com.aire.ux.ComponentInclusionVoter;
import com.aire.ux.ExtensionDefinition;

public class SelectionBasedComponentInclusionVoter implements ComponentInclusionVoter {

  @Override
  public boolean decide(ExtensionDefinition<?> extension) {
    return false;
  }
}
