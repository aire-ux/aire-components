package io.sunshower.zephyr.management;

import com.aire.ux.Host;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import io.sunshower.zephyr.ui.components.TabPanel;
import javax.annotation.security.PermitAll;
import lombok.Getter;

@Getter
@PermitAll
@Host("module-management")
@RoutePrefix("management")
@ParentLayout(ZephyrManagementConsoleView.class)
public class PluginTabView extends TabPanel {

  private final Tab moduleTab;
  private final Tab topologyTab;

  public PluginTabView() {
    super();
    this.moduleTab = this.addTab("Modules", ModuleGrid.class);
    this.topologyTab = this.addTab("Topology", TopologyView.class);
  }
}
