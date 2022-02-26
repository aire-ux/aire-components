package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import io.sunshower.zephyr.management.ModuleScheduleOverlay.Mode;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasReadyEvent;
import io.sunshower.zephyr.ui.canvas.CellAttributes;
import io.sunshower.zephyr.ui.canvas.Edge;
import io.sunshower.zephyr.ui.canvas.EdgeTemplate;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexTemplateAction;
import io.sunshower.zephyr.ui.canvas.actions.AddVerticesAction;
import io.sunshower.zephyr.ui.canvas.actions.ConnectVerticesAction;
import io.sunshower.zephyr.ui.canvas.actions.SetAllCellAttributesAction;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEvent;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.EventType;
import io.sunshower.zephyr.ui.components.ContextMenu;
import io.sunshower.zephyr.ui.components.Overlays;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import io.zephyr.kernel.Lifecycle;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.ModuleCoordinate;
import io.zephyr.kernel.module.ModuleLifecycle.Actions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import lombok.Getter;
import lombok.val;

@Breadcrumb(name = "Topology", host = MainView.class)
@Route(value = "modules/topology", layout = PluginTabView.class)
public class TopologyView extends AbstractModuleView
    implements ComponentEventListener<CanvasReadyEvent> {

  static final EdgeTemplate defaultEdgeTemplate;
  static final VertexTemplate defaultVertexTemplate;
  static final VertexTemplate defaultTaskTemplate;

  static {
    /** the default vertex template */
    defaultVertexTemplate =
        CondensationUtilities.read(
            VertexTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-node-template.json");

    /** the default task template */
    defaultTaskTemplate =
        CondensationUtilities.read(
            VertexTemplate.class,
            "classpath:canvas/resources/nodes/templates/task-node-template.json");

    /** the default edge template */
    defaultEdgeTemplate =
        CondensationUtilities.read(
            EdgeTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-edge-template.json");
  }

  /** immutable state */
  private final Model model;

  @Getter private final MenuBar menubar;
  @Getter private final ContextMenu<Vertex> canvasContextMenu;

  private final Registration onCanvasReadyRegistration;
  private final Map<State, List<Button>> actions;
  private final Map<Lifecycle.State, List<Button>> lifecycleGroups;
  /** mutable state */
  private Canvas canvas;

  @Inject
  public TopologyView(final Zephyr zephyr) {
    super(zephyr);

    actions = new EnumMap<>(State.class);
    lifecycleGroups = new EnumMap<>(Lifecycle.State.class);

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

  @Override
  protected Module getSelectedModule() {
    return selectedModule;
  }

  @Override
  protected ModuleLifecycleDelegate getModuleLifecycleDelegate() {
    return new ModuleLifecycleDelegate() {
      @Override
      public void select(Module module) {}

      @Override
      public void refresh() {}
    };
  }

  protected void updateMenuState() {
    val currentModule = getSelectedModule();
    val state = currentModule.getLifecycle().getState();
    val disabledStates = lifecycleGroups.get(state);
    getUI()
        .ifPresent(
            ui -> {
              ui.access(
                  () -> {
                    enableAll();
                    if (disabledStates != null) {
                      disable(disabledStates);
                    }
                    ui.push();
                  });
            });
  }

  private void disable(List<Button> toEnable) {
    for (val button : toEnable) {
      button.setEnabled(false);
    }
  }

  private void enableAll() {
    for (val state : Lifecycle.State.values()) {
      val toDisable = lifecycleGroups.get(state);
      if (toDisable != null) {
        for (val component : toDisable) {
          component.setEnabled(true);
        }
      }
    }
  }

  private void registerListeners() {
    canvas.addCanvasListener(
        CanvasEvent.CanvasInteractionEventType.Clicked,
        click -> {
          setSelectedModule(null);
          setEnabled(State.VertexSelected, false);
        });

    val highlightAttributes = new CellAttributes();
    val body = highlightAttributes.addAttribute("body", new HashMap<String, Serializable>());
    body.put("strokeWidth", 3);
    body.put("stroke", "#a366a3");

    val normalAttributes = new CellAttributes();
    val normalbody = normalAttributes.addAttribute("body", new HashMap<String, Serializable>());
    normalbody.put("strokeWidth", 1);
    normalbody.put("stroke", "#660066");

    canvas.addCellListener(
        EventType.MouseEnter,
        event -> {
          highlightAttributes.setId(event.getTarget().getId());
          canvas.invoke(SetAllCellAttributesAction.class, List.of(highlightAttributes));
        });

    canvas.addCellListener(
        EventType.MouseLeave,
        event -> {
          normalAttributes.setId(event.getTarget().getId());
          canvas.invoke(SetAllCellAttributesAction.class, List.of(normalAttributes));
        });

    canvas.addCellListener(
        EventType.Clicked,
        click -> {
          if (click.getTarget() instanceof Vertex) {
            val target = (Vertex) click.getTarget();
            val coordinate = ModuleCoordinate.parse(target.getKey());
            setSelectedModule(
                getZephyr().getPlugins().stream()
                    .filter(module -> Objects.equals(coordinate, module.getCoordinate()))
                    .findAny()
                    .orElse(null));
          }
          //          setEnabled(State.VertexSelected, true);
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
    getUI()
        .ifPresent(
            ui -> {
              ui.access(
                  () -> {
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
    registerAction(State.VertexSelected, item);
    item = createStopButtonMenu();
    action.getSubMenu().add(item);
    registerAction(State.VertexSelected, item);
    item = createRestartButtonMenu();
    action.getSubMenu().add(item);
    registerAction(State.VertexSelected, item);

    menuPlanAction = new Button("Plan", VaadinIcon.TASKS.create());
    menuPlanAction.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    var root = canvasContextMenu.getMenu().createRoot(menuPlanAction);
    item = createStartMenuButton();
    root.add(item);
    registerAction(State.VertexSelected, item);
    item = createStopButtonMenu();
    root.add(item);
    registerAction(State.VertexSelected, item);
    item = createRestartButtonMenu();
    root.add(item);
    registerAction(State.VertexSelected, item);
  }

  private void registerAction(State state, Button item) {
    actions.computeIfAbsent(state, (k) -> new ArrayList<>()).add(item);
  }

  private Button createStartMenuButton() {
    val button = new Button("Start", VaadinIcon.PLAY.create());
    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
    registerLifecycleItem(button, Lifecycle.State.Active);
    button.addClickListener(
        (ComponentEventListener<ClickEvent<Button>>)
            event -> {
              Overlays.open(
                  this,
                  ModuleScheduleOverlay.class,
                  Mode.Scheduling,
                  getZephyr().getKernel(),
                  getSelectedModule(),
                  Actions.Activate);
            });
    return button;
  }

  private Button createRestartButtonMenu() {
    val button = new Button("Restart", VaadinIcon.REFRESH.create());
    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    registerLifecycleItem(button, Lifecycle.State.Active);
    button.addClickListener(
        (ComponentEventListener<ClickEvent<Button>>)
            event -> {
              Overlays.open(
                  this,
                  ModuleScheduleOverlay.class,
                  Mode.Scheduling,
                  getZephyr().getKernel(),
                  getSelectedModule(),
                  Actions.Stop);
            });
    return button;
  }

  /**
   * when the selected module has one of the inactive states, then the button should be disabled
   *
   * @param button
   * @param inactiveStates
   */
  protected void registerLifecycleItem(Button button, Lifecycle.State... inactiveStates) {
    for (val state : inactiveStates) {
      lifecycleGroups.computeIfAbsent(state, s -> new ArrayList<>(1)).add(button);
    }
  }

  private Button createStopButtonMenu() {
    val button = new Button("Stop", VaadinIcon.STOP.create());
    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
    registerLifecycleItem(
        button, Lifecycle.State.Resolved, Lifecycle.State.Installed, Lifecycle.State.Uninstalled);
    button.addClickListener(
        (ComponentEventListener<ClickEvent<Button>>)
            event -> {
              Overlays.open(
                  this,
                  ModuleScheduleOverlay.class,
                  Mode.Scheduling,
                  getZephyr().getKernel(),
                  getSelectedModule(),
                  Actions.Stop);
            });
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

    for (val module : getZephyr().getPlugins()) {
      val vertex = new Vertex();
      vertex.setLabel(module.getCoordinate().getName());
      vertex.setKey(module.getCoordinate().toCanonicalForm());
      vertex.setTemplate(t);
      vertices.put(module.getCoordinate(), vertex);
    }

    for (val module : getZephyr().getPlugins()) {
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

  private ContextMenu<Vertex> createCanvasContextMenu() {
    val menu = canvas.createVertexContextMenu(EventType.ContextMenu);
    return menu;
  }

  public enum State {
    CanvasClicked,
    VertexSelected,
    EdgeSelected
  }

  private class PlanLifecycleEventListener
      implements ComponentEventListener<com.vaadin.flow.component.ClickEvent<Button>> {

    @Override
    public void onComponentEvent(ClickEvent<Button> event) {}
  }
}
