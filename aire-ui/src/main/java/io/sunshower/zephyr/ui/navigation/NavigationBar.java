package io.sunshower.zephyr.ui.navigation;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Nav;
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
    this.addClassName(direction.name().toLowerCase(Locale.ROOT));
  }

  public enum Direction {
    Vertical,
    Horizontal,
  }

}
