package io.sunshower.zephyr.management;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.controls.Breadcrumb;

@Breadcrumb(name = "Topology", host = MainView.class)
@Route(value = "modules/topology", layout = PluginTabView.class)
public class TopologyView extends VerticalLayout {

  public TopologyView() {
    this.setHeightFull();
    add(new Canvas());
  }
}
