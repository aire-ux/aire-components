package com.aire.ux;

public interface ComponentInclusionManager {

  Registration register(ComponentInclusionVoter voter);

  boolean decide(ExtensionDefinition<?> extension);
}
