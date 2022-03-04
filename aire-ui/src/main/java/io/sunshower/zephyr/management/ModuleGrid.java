package io.sunshower.zephyr.management;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Overlays;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Module;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.val;

@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport(value = "./styles/shared-styles.css", include = "lumo-badge")
@Route(value = "modules/list", layout = PluginTabView.class)
@Breadcrumb(name = "Modules", icon = "vaadin:plug", host = MainView.class)
public class ModuleGrid extends AbstractModuleView
    implements ValueChangeListener<ComponentValueChangeEvent<TextField, String>> {

  /** immutable state */
  private final Grid<Module> grid;

  /** mutable state */
  private TextField textField;

  @Inject
  @SuppressFBWarnings(justification = "Access pattern is safe")
  public ModuleGrid(@NonNull Zephyr zephyr) {
    super(zephyr);
    add(createMenubar());
    add(grid = populateGrid());
  }

  @Override
  public void valueChanged(ComponentValueChangeEvent<TextField, String> event) {
    val text = textField.getValue();
    val matches =
        getZephyr().getPlugins().stream()
            .filter(
                module ->
                    module.getCoordinate().getName().contains(text)
                        || module.getCoordinate().getGroup().contains(text)
                        || module.getCoordinate().getVersion().toString().contains(text))
            .collect(Collectors.toList());
    grid.setItems(new ListDataProvider<>(matches));
  }

  protected Module getSelectedModule() {
    val items = grid.getSelectedItems();
    if (items.isEmpty()) {
      return null;
    }
    return items.iterator().next();
  }

  @Override
  protected ModuleLifecycleDelegate getModuleLifecycleDelegate() {
    return new GridModuleLifecycleDelegate(grid);
  }

  @SuppressWarnings("RefusedBequest")
  protected void configureStyles() {
    getStyle().set("display", "flex");
    setHeight("100%");
  }

  private Component createMenubar() {
    val result = new MenuBar();
    result.addThemeVariants(
        MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE);
    textField = new TextField();
    textField.setPlaceholder("Search");
    textField.setClearButtonVisible(true);
    textField.setPrefixComponent(VaadinIcon.SEARCH.create());
    textField.setValueChangeMode(ValueChangeMode.EAGER);
    textField.addValueChangeListener(this);
    result.addItem(textField);
    addButtonsToMenubar(result);

    return result;
  }

  private void notifyCancel() {
    val notification = new Notification("Module Installation cancelled");
    notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    notification.setDuration(5000);
    notification.setPosition(Position.TOP_STRETCH);
    notification.open();
  }

  private void notifySuccess() {
    val notification = new Notification("Successfully uploaded modules!");
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    notification.setDuration(5000);
    notification.setPosition(Position.TOP_STRETCH);
    notification.open();
  }

  private Grid<Module> populateGrid() {
    val grid = new Grid<Module>();
    grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    grid.addItemClickListener(
        event -> {
          setSelectedModule(event.getItem());
        });

    grid.setMultiSort(true);
    grid.setAllRowsVisible(true);
    grid.setColumnReorderingAllowed(true);

    grid.addComponentColumn(
            (ValueProvider<Module, ModuleLifecycleButtonBar>)
                module ->
                    new ModuleLifecycleButtonBar(getModuleLifecycleDelegate(), getZephyr(), module))
        .setResizable(true)
        .setHeader("Lifecycle");
    grid.addColumn((ValueProvider<Module, String>) module -> module.getCoordinate().getGroup())
        .setSortable(true)
        .setResizable(true)
        .setHeader("Group");
    grid.addColumn((ValueProvider<Module, String>) module -> module.getCoordinate().getName())
        .setSortable(true)
        .setResizable(true)
        .setHeader("Name");
    grid.addColumn(
            (ValueProvider<Module, String>)
                module -> module.getCoordinate().getVersion().toString())
        .setHeader("Version");
    grid.addColumn(new ComponentRenderer<>(Span::new, new StatusComponentUpdater()))
        .setHeader("Status");
    grid.setItems(new ListDataProvider<>(getZephyr().getPlugins()));
    grid.getStyle().set("overflow-y", "auto");
    return grid;
  }

  private void addButtonsToMenubar(MenuBar result) {
    val button = new Button("Add Modules", VaadinIcon.PLUS.create());
    button.addClickListener(
        clickEvent -> {
          val overlay = Overlays.open(this, UploadPluginOverlay.class);
          overlay.addOverlayClosedEventListener(
              closed -> {
                if (!closed.isCancelled()) {
                  grid.setItems(new ListDataProvider<>(getZephyr().getPlugins()));
                  notifySuccess();
                } else {
                  notifyCancel();
                }
              });
        });
    result.addItem(button);
  }

  private static class StatusComponentUpdater implements SerializableBiConsumer<Span, Module> {

    @Override
    public void accept(Span span, Module module) {
      val state = module.getLifecycle().getState();
      val themeList = span.getElement().getThemeList();
      switch (state) {
        case Active:
          themeList.add("badge success");
          break;
        case Failed:
          themeList.add("badge error");
          break;
        default: // Resolved, Installed, Uninstalled:
          themeList.add("badge");
          break;
      }
      span.setText(state.name());
    }
  }
}
