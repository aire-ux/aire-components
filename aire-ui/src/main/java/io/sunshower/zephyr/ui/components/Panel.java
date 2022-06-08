package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.RouterLayout;
import io.sunshower.zephyr.ui.navigation.NavigationBar;

@Tag("aire-panel")
@JsModule("./aire/ui/components/panel.ts")
@CssImport("./styles/aire/ui/components/panel.css")
public class Panel extends HtmlContainer implements RouterLayout {

  public Panel(Component... components) {
    add(components);
    setMode(Mode.Panel);
  }

  public void setMode(Mode mode) {
    switch (mode) {
      case Grid:
        getElement().getClassList().remove("panel");
        getElement().getClassList().add("grid");
        break;
      case Panel:
        getElement().getClassList().remove("grid");
        getElement().getClassList().add("panel");
        break;
    }
  }

  public void setNavigationBar(NavigationBar navigationBar) {
    navigationBar.getElement().setAttribute("slot", "navigation-bar");
    add(navigationBar);
  }

  public void removeNavigationBar(NavigationBar navigationBar) {
    remove(navigationBar);
  }

  public enum Mode {
    Grid,
    Panel
  }
}
