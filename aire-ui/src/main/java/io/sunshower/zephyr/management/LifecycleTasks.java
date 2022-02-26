package io.sunshower.zephyr.management;

import io.sunshower.zephyr.ui.canvas.CellTemplate;
import io.sunshower.zephyr.ui.canvas.Edge;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.zephyr.kernel.Coordinate;
import io.zephyr.kernel.concurrency.Process;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.core.actions.plugin.ModuleLifecycleTask;
import io.zephyr.kernel.module.ModuleLifecycle;
import io.zephyr.kernel.module.ModuleLifecycleChangeGroup;
import io.zephyr.kernel.module.ModuleLifecycleChangeRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.val;

public class LifecycleTasks {

  /**
   * create a process for the provided lifecycle tasks
   *
   * @param kernel the kernel to schedule them against
   * @param model the model to add the vertices and edges to
   * @param edgeTemplate
   * @param vertexTemplate
   * @param action
   * @param coordinates
   * @return
   */
  public static LifecycleTaskDescriptor createProcess(
      Kernel kernel,
      Model model,
      CellTemplate edgeTemplate,
      VertexTemplate vertexTemplate,
      ModuleLifecycle.Actions action,
      Coordinate... coordinates) {

    val changeGroup = new ModuleLifecycleChangeGroup();
    for (val coordinate : coordinates) {
      val changeRequest = new ModuleLifecycleChangeRequest(coordinate, action);
      changeGroup.addRequest(changeRequest);
    }
    val prepped = kernel.getModuleManager().prepare(changeGroup);
    val process = prepped.getProcess();
    val tasks = process.getTasks();

    val iterator = tasks.iterator();
    val vertices = new ArrayList<Vertex>();
    val edges = new ArrayList<io.sunshower.zephyr.ui.canvas.Edge>();
    val joinPoints = new ArrayList<Vertex>();
    while (iterator.hasNext()) {
      val taskSet = iterator.next();
      val joinPoint = new Vertex();
      joinPoint.setShape("circle");
      vertices.add(joinPoint);
      for (val task : taskSet.getTasks()) {
        val taskVertex = new Vertex();
        if (task.getValue() instanceof ModuleLifecycleTask v) {
          taskVertex.setKey(v.getCoordinate().toCanonicalForm());
        }
        taskVertex.setTemplate(vertexTemplate);
        vertices.add(taskVertex);
        val edge = model.connect(taskVertex, joinPoint);
        edge.setCellTemplate(edgeTemplate);
        edges.add(edge);
        if (!joinPoints.isEmpty()) {
          val pjp = joinPoints.get(joinPoints.size() - 1);
          val e = model.connect(pjp.getId(), edge.getSource());
          e.setCellTemplate(edgeTemplate);
          edges.add(e);
        }
      }
      joinPoints.add(joinPoint);
    }
    return new LifecycleTaskDescriptor(edges, vertices, process, action);
  }

  @Data
  public static class LifecycleTaskDescriptor {

    private final List<Edge> edges;
    private final List<Vertex> vertices;
    private final Process<String> process;
    private final ModuleLifecycle.Actions action;
  }
}
