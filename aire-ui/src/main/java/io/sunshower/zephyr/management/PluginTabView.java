package io.sunshower.zephyr.management;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import io.sunshower.zephyr.ui.components.TabPanel;

@RoutePrefix("management")
@ParentLayout(ZephyrManagementConsoleView.class)
public class PluginTabView extends TabPanel {


  public PluginTabView() {
    super();
    this.addTab("Modules", ModuleGrid.class);
    this.addTab("Topology", TopologyView.class);
  }

}
