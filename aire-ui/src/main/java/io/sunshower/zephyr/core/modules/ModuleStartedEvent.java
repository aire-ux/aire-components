package io.sunshower.zephyr.core.modules;

import io.zephyr.kernel.Module;

public class ModuleStartedEvent extends ModuleLifecycleEvent {

  public ModuleStartedEvent(Module source) {
    super(source);
  }
}
