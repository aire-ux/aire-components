package io.sunshower.zephyr.management;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import javax.inject.Inject;

@Breadcrumb(name = "Topology", host = MainView.class)
@Route(value = "modules/topology", layout = PluginTabView.class)
public class TopologyView extends VerticalLayout {

  private Canvas canvas;
  private final Model model;
  private final Zephyr zephyr;

  @Inject
  public TopologyView(final Zephyr zephyr) {
    this.setHeightFull();
    this.zephyr = zephyr;
    this.canvas = new Canvas();
    this.model = Model.create(canvas);
    add(canvas);
  }

}
