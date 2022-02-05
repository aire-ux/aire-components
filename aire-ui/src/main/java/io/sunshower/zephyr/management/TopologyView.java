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
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexTemplateAction;
import io.sunshower.zephyr.ui.canvas.actions.AddVerticesAction;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Module;
import java.util.stream.Collectors;
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
    val template = CondensationUtilities.read(
        VertexTemplate.class,
        "classpath:canvas/resources/nodes/templates/module-node-template.json"
    );
    canvas.invoke(AddVertexTemplateAction.class, template).toFuture()
        .thenAccept(this::configureModuleNodes);
  }

  private void configureModuleNodes(VertexTemplate t) {
    val nodes = zephyr.getPlugins()
        .stream()
        .map(module -> this.nodeFromModule(module, t))
        .collect(Collectors.toList());
    canvas.invoke(AddVerticesAction.class, nodes);
  }

  private Vertex nodeFromModule(Module module, VertexTemplate t) {
    val vertex = new Vertex();
    vertex.setTemplate(t);
    return vertex;
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    onCanvasReadyRegistration.remove();
  }
}
