package io.sunshower.zephyr.core.modules;

import io.zephyr.kernel.Module;

public class ModuleStoppedEvent extends ModuleLifecycleEvent{

  public ModuleStoppedEvent(Module source) {
    super(source);
  }
}
