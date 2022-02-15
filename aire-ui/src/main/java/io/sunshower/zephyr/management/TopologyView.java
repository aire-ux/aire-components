package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
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
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEvent;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.EventType;
import io.sunshower.zephyr.ui.components.ContextMenu;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
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
  @Getter
  private final MenuBar menubar;
  @Getter
  private final ContextMenu<Vertex> canvasContextMenu;
  private final Registration onCanvasReadyRegistration;

  /**
   * mutable state
   */
  private Canvas canvas;

  private Map<State, List<Button>> actions;

  @Inject
  public TopologyView(final Zephyr zephyr) {

    actions = new EnumMap<>(State.class);

    /** initialize state */
    this.zephyr = zephyr;
    this.canvas = new Canvas();
    this.model = Model.create(canvas);
    this.menubar = createMenuBar();
    this.canvasContextMenu = createCanvasContextMenu();
    this.onCanvasReadyRegistration = canvas.addOnCanvasReadyListener(this);

    configureActions();

    registerListeners();

    /** configure component */
    configureStyles();
    setHeightFull();
    add(menubar);
    add(canvas);
    setEnabled(State.VertexSelected, false);
  }

  private void registerListeners() {
    canvas.addCanvasListener(
        CanvasEvent.CanvasInteractionEventType.Clicked,
        click -> {
          setEnabled(State.VertexSelected, false);
        });
    canvas.addCellListener(
        EventType.Clicked,
        click -> {
          setEnabled(State.VertexSelected, true);
        });
  }

  private MenuBar createMenuBar() {
    val result = new MenuBar();
    result.getStyle().set("align-self", "flex-start");
    result.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
    return result;
  }

  private void configureActions() {
    createPlanMenus();
  }

  private void setEnabled(State state, boolean enabled) {
    val items = actions.get(state);
    getUI().ifPresent(ui -> {
      ui.access(() -> {
        for (val item : items) {
          item.setEnabled(enabled);
        }
      });
    });
  }

  private void createPlanMenus() {
    var menuPlanAction = new Button("Plan", VaadinIcon.TASKS.create());
    menuPlanAction.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    val action = menubar.addItem(menuPlanAction);
    var item = createStartMenuButton();
    action.getSubMenu().add(item);
    item.addClickListener(new PlanLifecycleEventListener());
    registerAction(State.VertexSelected, item);

    menuPlanAction = new Button("Plan", VaadinIcon.TASKS.create());
    menuPlanAction.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    var root = canvasContextMenu.getMenu().createRoot(menuPlanAction);
    item = createStartMenuButton();
    root.add(item);

    registerAction(State.VertexSelected, item);
  }

  private void registerAction(State state, Button item) {
    actions.computeIfAbsent(state, (k) -> new ArrayList<>()).add(item);
  }

  private Button createStartMenuButton() {
    val button = new Button("Start", VaadinIcon.PLAY.create());
    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
    return button;
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
        .then(result -> canvas.invoke(ConnectVerticesAction.class, edges));
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    onCanvasReadyRegistration.remove();
  }

  private ContextMenu<Vertex> createCanvasContextMenu() {
    val menu = canvas.createVertexContextMenu(EventType.ContextMenu);
    return menu;
  }

  private void configureStyles() {
    val style = this.getStyle();
    style.set("display", "flex");
    style.set("justify-content", "center");
    style.set("align-items", "center");
  }

  public enum State {
    CanvasClicked,
    VertexSelected,
    EdgeSelected
  }

  private class PlanLifecycleEventListener
      implements ComponentEventListener<com.vaadin.flow.component.ClickEvent<Button>> {

    @Override
    public void onComponentEvent(ClickEvent<Button> event) {
    }
  }
}
