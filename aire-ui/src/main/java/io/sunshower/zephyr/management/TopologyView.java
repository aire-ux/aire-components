package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasClickedEvent;
import io.sunshower.zephyr.ui.canvas.CanvasReadyEvent;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexAction;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import javax.inject.Inject;
import lombok.val;

@Breadcrumb(name = "Topology", host = MainView.class)
@Route(value = "modules/topology", layout = PluginTabView.class)
public class TopologyView extends VerticalLayout
    implements ComponentEventListener<CanvasReadyEvent> {

  private final Model model;
  private final Zephyr zephyr;
  private final Registration onCanvasReadyRegistration;
  private Canvas canvas;

  @Inject
  public TopologyView(final Zephyr zephyr) {
    this.setHeightFull();
    this.zephyr = zephyr;
    this.canvas = new Canvas();
    this.model = Model.create(canvas);
    add(canvas);
    onCanvasReadyRegistration = canvas.addOnCanvasReadyListener(this);
    canvas.addOnCanvasClickedEventListener((ComponentEventListener<CanvasClickedEvent>) event -> {
      val vertex = new Vertex();
      val click = event.getValue();
      vertex.setX(click.getX());
      vertex.setY(click.getY());
      vertex.setWidth(100d);
      vertex.setHeight(100d);
      new AddVertexAction(vertex).apply(model);
    });
  }


  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    onCanvasReadyRegistration.remove();
  }
}
