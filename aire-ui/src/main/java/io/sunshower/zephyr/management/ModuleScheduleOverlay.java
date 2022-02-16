package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.shared.Registration;
import io.sunshower.gyre.DirectedGraph.Edge;
import io.sunshower.gyre.TaskSet;
import io.sunshower.zephyr.spring.Dynamic;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasReadyEvent;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.canvas.actions.AddVerticesAction;
import io.sunshower.zephyr.ui.canvas.actions.ConnectVerticesAction;
import io.sunshower.zephyr.ui.components.Overlay;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.concurrency.Task;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.module.ModuleLifecycle.Actions;
import io.zephyr.kernel.module.ModuleLifecycleChangeGroup;
import io.zephyr.kernel.module.ModuleLifecycleChangeRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

public class ModuleScheduleOverlay extends Overlay implements
    ComponentEventListener<CanvasReadyEvent> {

  private final Mode mode;
  private final Kernel kernel;
  private final Module module;
  private final Canvas canvas;
  private final Registration onReadyRegistration;

  @Dynamic
  public ModuleScheduleOverlay(
      @Dynamic Mode mode,
      @Dynamic Kernel kernel,
      @Dynamic Module module
  ) {
    this.mode = mode;
    this.kernel = kernel;
    this.module = module;
    addHeader();
    this.canvas = addBody();
    this.onReadyRegistration = canvas.addOnCanvasReadyListener(this);
  }

  private Canvas addBody() {
    val content = getContent();
    val canvas = new Canvas();
    content.add(canvas);
    return canvas;
  }

  private void computeSchedule(Canvas canvas) {
    val changeGroup = new ModuleLifecycleChangeGroup();
    val changeRequest = new ModuleLifecycleChangeRequest(module.getCoordinate(), Actions.Stop);
    changeGroup.addRequest(changeRequest);
    val prepped = kernel.getModuleManager().prepare(changeGroup);
    val tasks = prepped.getProcess().getTasks();
    layoutTasks(tasks, canvas);
  }

  private void layoutTasks(List<TaskSet<Edge<String>, Task>> tasks, Canvas canvas) {
    val model = canvas.getModel();
    val iterator = tasks.iterator();
    val vertices = new ArrayList<Vertex>();
    val edges = new ArrayList<io.sunshower.zephyr.ui.canvas.Edge>();
    while (iterator.hasNext()) {
      val taskSet = iterator.next();
      val joinPoint = new Vertex();
      joinPoint.setShape("rect");
      joinPoint.setWidth(40d);
      joinPoint.setHeight(40d);
      joinPoint.setX(20d);
      joinPoint.setY(20d);
      vertices.add(joinPoint);
      for (val task : taskSet.getTasks()) {
        val taskVertex = new Vertex();
        taskVertex.setWidth(40d);
        taskVertex.setHeight(40d);
        taskVertex.setX(20d);
        taskVertex.setY(20d);
        taskVertex.setShape("rect");
        vertices.add(taskVertex);
        edges.add(model.connect(taskVertex, joinPoint));
      }
    }
    canvas.invoke(AddVerticesAction.class, vertices)
        .then(e -> canvas.invoke(ConnectVerticesAction.class, edges));
  }

  private void addHeader() {
    val header = getHeader();
    header.add(
        new H1(String.format("%s dependency graph for %s",
            mode == Mode.Executing ? "Executing" : "Planning",
            module.getCoordinate().toCanonicalForm())));
    header.add(getCloseButton());
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    computeSchedule(canvas);
  }

  public enum Mode {
    Scheduling,
    Executing
  }

}
