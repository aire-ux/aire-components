package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Aside;
import com.vaadin.flow.shared.Registration;
import java.util.Locale;

@Tag("aire-drawer")
@JsModule("./aire/ui/components/drawer.ts")
@CssImport("./styles/aire/ui/components/drawer.css")
public class Drawer extends Aside {

  private final Direction direction;

  public Drawer() {
    this(Direction.Vertical);
  }

  public Drawer(Direction direction) {
    this.direction = direction;
    addClassName(direction.name().toLowerCase(Locale.ROOT));
    getElement().setAttribute("slot", "drawer");
  }

  public Registration addDrawerOpenedEventListener(
      ComponentEventListener<DrawerOpenedEvent> listener) {
    return addListener(DrawerOpenedEvent.class, listener);
  }

  public Registration addDrawerClosedEventListener(
      ComponentEventListener<DrawerClosedEvent> listener) {
    return addListener(DrawerClosedEvent.class, listener);
  }

  public enum Direction {
    Vertical,
    Horizontal,
    VerticalRight,
  }
}
