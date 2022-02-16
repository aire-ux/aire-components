package io.sunshower.zephyr.management;

import com.vaadin.flow.component.grid.Grid;
import io.zephyr.kernel.Module;

class GridModuleLifecycleDelegate implements ModuleLifecycleDelegate {

  final Grid<Module> grid;

  GridModuleLifecycleDelegate(Grid<Module> grid) {
    this.grid = grid;
  }

  @Override
  public void select(Module module) {
    grid.select(module);
  }

  @Override
  public void refresh() {
    grid.getDataProvider().refreshAll();
  }
}
