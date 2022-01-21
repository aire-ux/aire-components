package io.sunshower.zephyr.management;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.sunshower.zephyr.ui.components.Property;
import io.sunshower.zephyr.ui.components.PropertyPanel;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Module;
import java.util.List;
import java.util.function.Supplier;
import lombok.val;

public class ModuleInfoPanel extends VerticalLayout {

  private final Zephyr zephyr;

  public ModuleInfoPanel(final Zephyr zephyr, final Supplier<Module> module) {
    this.zephyr = zephyr;
    setPadding(false);
    setModule(module);
  }

  private void setModule(Supplier<Module> coordinate) {
    val propertyPanel = new PropertyPanel(VaadinIcon.INFO.create(), "Module Info");
    add(propertyPanel);
    val c = coordinate.get();

    if (c != null) {
      val coord = c.getCoordinate();
      val properties =
          List.of(
              new Property("group", coord.getGroup()),
              new Property("name", coord.getName()),
              new Property("version", coord.getVersion().toString()));
      propertyPanel.setProperties(properties);
    } else {
      propertyPanel.setContent(new Label("please select a module"));
    }
  }
}
