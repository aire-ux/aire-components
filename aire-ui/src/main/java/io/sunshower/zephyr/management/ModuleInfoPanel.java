package io.sunshower.zephyr.management;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.sunshower.zephyr.ui.components.Property;
import io.sunshower.zephyr.ui.components.PropertyPanel;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import java.util.List;
import java.util.function.Supplier;
import lombok.val;

public class ModuleInfoPanel extends VerticalLayout {

  private final Zephyr zephyr;

  public ModuleInfoPanel(final Zephyr zephyr, final Supplier<Coordinate> coordinate) {
    this.zephyr = zephyr;
    setPadding(false);
    setCoordinate(coordinate);
  }

  private void setCoordinate(Supplier<Coordinate> coordinate) {
    val propertyPanel = new PropertyPanel(VaadinIcon.INFO.create(), "Module Info");
    add(propertyPanel);
    val c = coordinate.get();
    if(c != null) {
      val properties = List.of(
          new Property("group", c.getGroup()),
          new Property("name", c.getName()),
          new Property("version", c.getVersion().toString())
      );
      propertyPanel.setProperties(properties);
    } else {
      propertyPanel.setContent(new Label("please select a module"));
    }

  }
}
