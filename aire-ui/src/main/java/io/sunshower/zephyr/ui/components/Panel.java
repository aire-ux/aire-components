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
  }

  public void setNavigationBar(NavigationBar navigationBar) {
    navigationBar.getElement().setAttribute("slot", "navigation-bar");
    add(navigationBar);
  }

  public void removeNavigationBar(NavigationBar navigationBar) {
    remove(navigationBar);
  }
}
