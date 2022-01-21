package io.sunshower.zephyr.management;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route(value = "modules/topology", layout = PluginTabView.class)
public class TopologyView extends Div {

  public TopologyView() {
    add(new Button("Topology"));
  }
}
