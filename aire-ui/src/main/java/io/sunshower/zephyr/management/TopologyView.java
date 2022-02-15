package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasReadyEvent;
import io.sunshower.zephyr.ui.canvas.Edge;
import io.sunshower.zephyr.ui.canvas.EdgeTemplate;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexTemplateAction;
import io.sunshower.zephyr.ui.canvas.actions.AddVerticesAction;
import io.sunshower.zephyr.ui.canvas.actions.ConnectVerticesAction;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.EventType;
import io.sunshower.zephyr.ui.components.ContextMenu;
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

  static final EdgeTemplate defaultEdgeTemplate;
  static final VertexTemplate defaultVertexTemplate;

  static {
    defaultVertexTemplate =
        CondensationUtilities.read(
            VertexTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-node-template.json");
    defaultEdgeTemplate =
        CondensationUtilities.read(
            EdgeTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-edge-template.json");
  }

  /**
   * immutable state
   */
  private final Model model;

  private final Zephyr zephyr;
  private final MenuBar menubar;
  private final ContextMenu<Vertex> canvasContextMenu;
  private final Registration onCanvasReadyRegistration;
  private final Registration onVertexClickedRegistration;

  /**
   * mutable state
   */
  private Canvas canvas;

  @Inject
  public TopologyView(final Zephyr zephyr) {
    this.zephyr = zephyr;
    this.canvas = new Canvas();
    this.model = Model.create(canvas);
    this.menubar = createMenubar();
    configureStyles();
    setHeightFull();
    add(canvas);
    canvasContextMenu = createCanvasContextMenu();
    onCanvasReadyRegistration = canvas.addOnCanvasReadyListener(this);
    onVertexClickedRegistration = canvas.addVertexListener(EventType.Clicked, System.out::println);
//    this.actionMap = ActionMap.read(TopologyView.class);
//    canvas.addVertexListener(EventType.ContextMenu)
  }

  private MenuBar createMenubar() {
    return new MenuBar();
  }


  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    canvas
        .invoke(AddVertexTemplateAction.class, defaultVertexTemplate)
        .toFuture()
        .thenAccept(this::configureModuleNodes);
  }

  private void configureModuleNodes(VertexTemplate t) {
    val edges = new ArrayList<Edge>();
    val vertices = new HashMap<Coordinate, Vertex>();

    for (val module : zephyr.getPlugins()) {
      val vertex = new Vertex();
      vertex.setLabel(module.getCoordinate().toCanonicalForm());
      vertex.setTemplate(t);
      vertices.put(module.getCoordinate(), vertex);
    }

    for (val module : zephyr.getPlugins()) {
      for (val dependency : module.getDependencies()) {
        val target = vertices.get(dependency.getCoordinate());
        val source = vertices.get(module.getCoordinate());
        val edge = new Edge(source.getId(), target.getId(), defaultEdgeTemplate);
        edges.add(edge);
      }
    }
    canvas
        .invoke(AddVerticesAction.class, new ArrayList<>(vertices.values()))
        .then(
            result -> canvas.invoke(ConnectVerticesAction.class, edges));
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    onCanvasReadyRegistration.remove();
    onVertexClickedRegistration.remove();
  }

  private ContextMenu<Vertex> createCanvasContextMenu() {
    val menu = canvas.createVertexContextMenu(EventType.ContextMenu);
    createPlanSubmenu(menu);
    createExecuteSubmenu(menu);
    return menu;
  }

  private void createPlanSubmenu(ContextMenu<Vertex> menu) {

    val rootButton = new Button("Plan", VaadinIcon.TASKS.create());
    rootButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    val root = menu.getMenu().createRoot(rootButton);
    val start = new Button("Start", VaadinIcon.PLAY.create());
    start.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
    root.add(start);

    val stop = new Button("Stop", VaadinIcon.STOP.create());
    stop.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

    val restart = new Button("Restart", VaadinIcon.REFRESH.create());
    restart.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    root.add(start, stop);
    root.createRoot(restart).add(new Button("Sup"));
  }

  private void createExecuteSubmenu(ContextMenu<Vertex> menu) {

    val rootButton = new Button("Execute", VaadinIcon.CURLY_BRACKETS.create());
    rootButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    val root = menu.getMenu().createRoot(rootButton);
    val start = new Button("Start", VaadinIcon.PLAY.create());
    start.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
    root.add(start);

    val stop = new Button("Stop", VaadinIcon.STOP.create());
    stop.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

    val restart = new Button("Restart", VaadinIcon.REFRESH.create());
    restart.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    root.add(start, stop, restart);
  }

  private void configureStyles() {
    val style = this.getStyle();
    style.set("display", "flex");
    style.set("justify-content", "center");
    style.set("align-items", "center");
  }
}
