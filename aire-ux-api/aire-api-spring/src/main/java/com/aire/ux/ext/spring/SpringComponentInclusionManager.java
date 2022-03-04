package com.aire.ux.ext.spring;

import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.ComponentInclusionVoter;
import com.aire.ux.ExtensionDefinition;
import com.aire.ux.Registration;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SpringComponentInclusionManager implements ComponentInclusionManager {

  private final List<ComponentInclusionVoter> componentInclusionVoters;

  public SpringComponentInclusionManager() {
    this.componentInclusionVoters = new ArrayList<>();
  }

  @Override
  public Registration register(ComponentInclusionVoter voter) {
    synchronized (componentInclusionVoters) {
      componentInclusionVoters.add(voter);
      return () -> componentInclusionVoters.remove(voter);
    }
  }

  @Override
  public boolean decide(ExtensionDefinition<?> extension) {
    return componentInclusionVoters.stream().allMatch(voter -> voter.decide(extension));
  }
}
