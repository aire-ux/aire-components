package io.sunshower.zephyr.ui.navigation;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Nav;
import io.sunshower.zephyr.ui.components.Drawer;
import java.util.Locale;
import lombok.NonNull;

@Tag("aire-navigation-bar")
@JsModule("./aire/ui/navigation/navigation-bar.ts")
@CssImport("./styles/aire/ui/navigation/navigation-bar.css")
public class NavigationBar extends Nav {

  private final Direction direction;

  public NavigationBar() {
    this(Direction.Horizontal);
  }

  public NavigationBar(@NonNull Direction direction) {
    this.direction = direction;
    getElement().setAttribute("slot", "drawer");
    addClassName(direction.name().toLowerCase(Locale.ROOT));
  }

  public void setDrawer(Drawer drawer) {
    getChildren().filter(child -> child instanceof Drawer).findAny().ifPresent(this::remove);
    addComponentAsFirst(drawer);
  }

  public enum Direction {
    Vertical,
    VerticalRight,
    Horizontal,
  }
}
