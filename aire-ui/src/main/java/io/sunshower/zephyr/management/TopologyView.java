package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasReadyEvent;
import io.sunshower.zephyr.ui.canvas.Edge;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexTemplateAction;
import io.sunshower.zephyr.ui.canvas.actions.AddVerticesAction;
import io.sunshower.zephyr.ui.canvas.actions.ConnectVerticesAction;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;
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
    //    model.addNodeTemplate(new ResourceNodeTemplate(""));
    add(canvas);
    onCanvasReadyRegistration = canvas.addOnCanvasReadyListener(this);
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    val template =
        CondensationUtilities.read(
            VertexTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-node-template.json");
    canvas
        .invokeAsynchronously(AddVertexTemplateAction.class, template)
        .toFuture()
        .thenAccept(this::configureModuleNodes);
  }

  private void configureModuleNodes(VertexTemplate t) {
    val edges = new ArrayList<Edge>();
    val vertices = new HashMap<Coordinate, Vertex>();

    for (val module : zephyr.getPlugins()) {
      val vertex = new Vertex();
      vertex.setTemplate(t);
      vertices.put(module.getCoordinate(), vertex);
    }

    for (val module : zephyr.getPlugins()) {
      for (val dependency : module.getDependencies()) {
        val target = vertices.get(dependency.getCoordinate());
        val source = vertices.get(module.getCoordinate());
        edges.add(new Edge(source.getId(), target.getId()));
      }
    }
    canvas.invokeAsynchronously(AddVerticesAction.class, new ArrayList<>(vertices.values()))
        .then(result -> {
          canvas.invokeAsynchronously(ConnectVerticesAction.class, edges);
        });
  }


  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    onCanvasReadyRegistration.remove();
  }
}
