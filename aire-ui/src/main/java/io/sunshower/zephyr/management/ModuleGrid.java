package io.sunshower.zephyr.management;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import io.zephyr.kernel.core.ModuleCoordinate;
import io.zephyr.kernel.core.SemanticVersion;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.val;

@Route(value = "modules/list", layout = PluginTabView.class)
public class ModuleGrid extends VerticalLayout {

  private final Zephyr zephyr;

  @Inject
  public ModuleGrid(@NonNull Zephyr zephyr) {
    this.zephyr = zephyr;
    this.setHeight("100%");
    add(createMenubar());
    add(populateGrid());
  }

  private Component createMenubar() {
    val result = new MenuBar();
    result.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_ICON,
        MenuBarVariant.LUMO_TERTIARY_INLINE);
    val textField = new TextField();
    textField.setPlaceholder("Search");
    textField.setClearButtonVisible(true);
    textField.setPrefixComponent(VaadinIcon.SEARCH.create());
    result.addItem(textField);

    val button = new Button("Add Modules", VaadinIcon.PLUS.create());
//    button.addThemeVariants(ButtonVariant.LUMO_SMALL);
    result.addItem(button);

    return result;
  }


  private Grid<Coordinate> populateGrid() {

    val grid = new Grid<Coordinate>();
    grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    grid.addColumn(Coordinate::getGroup).setHeader("Group");
    grid.addColumn(Coordinate::getName).setHeader("Name");
    grid.addColumn(Coordinate::getVersion).setHeader("Version");
    val items = getPluginCoordinates();
    items.addAll(zephyr.getPluginCoordinates());
    grid.setItems(items);

    return grid;
  }

  List<Coordinate> getPluginCoordinates() {
    val result = new ArrayList<Coordinate>();
    for (int i = 0; i < 100; i++) {
      val coordinate = new ModuleCoordinate("test-plugin" + i, "test",
          new SemanticVersion("1.0.0"));

      result.add(coordinate);
    }
    return result;
  }
}
