package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;

@Tag("aire-navigation-bar-button")
@JsModule("./aire/ui/controls/navigation-bar-button.ts")
@CssImport("./styles/aire/ui/controls/navigation-bar-button.css")
public class DrawerNavigationBarButton extends HtmlContainer implements
    ClickNotifier<DrawerNavigationBarButton>,
    Focusable<DrawerNavigationBarButton> {

  private final Icon icon;
  private final String text;


  public DrawerNavigationBarButton(
      Icon icon,
      String text
  ) {
    this.icon = icon;
    this.text = text;
    add(icon);
    add(text);
    addClassName("aire-drawer-button");
    getElement().setAttribute("rotate", "true");
  }

}
