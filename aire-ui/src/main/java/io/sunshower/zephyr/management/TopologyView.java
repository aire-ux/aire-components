package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasReadyEvent;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.val;

@Breadcrumb(name = "Topology", host = MainView.class)
@Route(value = "modules/topology", layout = PluginTabView.class)
public class TopologyView extends VerticalLayout
    implements ComponentEventListener<CanvasReadyEvent> {

  private final Model model;
  private final Zephyr zephyr;
  private Canvas canvas;
  private final Registration onCanvasReadyRegistration;

  @Inject
  public TopologyView(final Zephyr zephyr) {
    this.setHeightFull();
    this.zephyr = zephyr;
    this.canvas = new Canvas();
    this.model = Model.create(canvas);
    createGraph();
    add(canvas);
    onCanvasReadyRegistration = canvas.addOnCanvasReadyListener(this);
  }

  private void createGraph() {
    val vertices = new ArrayList<Vertex>();
    int i = 0;
    for (val coordinate : zephyr.getPluginCoordinates()) {
      vertices.add(vertexFrom(coordinate, ++i));
    }
    model.setVertices(vertices);
  }

  private Vertex vertexFrom(Coordinate coordinate, int i) {
    val vertex = new Vertex();
    vertex.setWidth(100f);
    vertex.setHeight(100f);
    vertex.setX(i * 100f);
    vertex.setY(i * 100f);
    vertex.setLabel(coordinate.toCanonicalForm());
    return vertex;
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    //    new AddVertexTemplateAction(UI::getCurrent, template).apply(model);
    //    new AddVerticesAction(UI::getCurrent, convertModulesToVertices()).apply(model);
  }

  private List<Vertex> convertModulesToVertices() {
    return zephyr.getPluginCoordinates().stream()
        .map(this::convertModuleToVertex)
        .collect(Collectors.toList());
  }

  private Vertex convertModuleToVertex(Coordinate coordinate) {
    val vertex = new Vertex();
    vertex.setX(100f);
    vertex.setY(100f);
    return vertex;
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    onCanvasReadyRegistration.remove();
  }
}
