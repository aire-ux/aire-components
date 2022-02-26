package io.sunshower.zephyr.core.modules;

import io.zephyr.kernel.Module;
import org.springframework.context.ApplicationEvent;

public class ModuleLifecycleEvent extends ApplicationEvent {

  public ModuleLifecycleEvent(Module source) {
    super(source);
  }

  public Module getSource() {
    return (Module) super.getSource();
  }
}
