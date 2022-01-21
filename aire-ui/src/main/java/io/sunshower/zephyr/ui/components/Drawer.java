package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Aside;
import com.vaadin.flow.shared.Registration;
import java.util.Locale;
import lombok.val;

@Tag("aire-drawer")
@JsModule("./aire/ui/components/drawer.ts")
@CssImport("./styles/aire/ui/components/drawer.css")
public class Drawer extends Aside {

  private static PropertyDescriptor<String, String> STATE;

  static {
    STATE = PropertyDescriptors.propertyWithDefault("state", State.Closed.getName());
  }

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

  public boolean isOpen() {
    return getState() == State.Open;
  }

  public boolean isClosed() {
    return getState() == State.Closed;
  }

  public State getState() {
    return State.parse(STATE.get(this));
  }

  public void toggle() {
    if (isOpen()) {
      close();
    } else {
      open();
    }
  }

  public void close() {
    STATE.set(this, State.Closed.getName());
  }

  public void open() {
    STATE.set(this, State.Open.getName());

  }

  public void setContent(Component component) {
    UI.getCurrent().access(() -> {
      removeAll();
      add(component);
    });
  }

  public enum State {
    Open(0),
    Closed(1);

    final int value;

    State(int value) {
      this.value = value;
    }

    public static State parse(String s) {
      if (s == null) {
        return Closed;
      }
      val normalized = s.trim().toLowerCase(Locale.ROOT);
      switch (normalized) {
        case "open":
          return Open;
        case "closed":
          return Closed;
        default:
          throw new IllegalArgumentException("No such state: [" + s + "]");
      }
    }

    public String getName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }

  public enum Direction {
    Vertical,
    Horizontal,
    VerticalRight,
  }
}
