package io.sunshower.zephyr.management;

import io.zephyr.kernel.Module;

public interface ModuleLifecycleDelegate {

  void select(Module module);

  void refresh();
}
