package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("aire-navigation-bar-button")
@JsModule("./aire/ui/controls/navigation-bar-button.ts")
@CssImport("./styles/aire/ui/controls/navigation-bar-button.css")
public class NavigationBarButton extends HtmlContainer implements
    ClickNotifier<NavigationBarButton> {

  private final Class<? extends Component> route;


  public NavigationBarButton(Class<? extends Component> route) {
    this.route = route;
  }

  public NavigationBarButton(Class<? extends Component> route, Component... components) {
    add(components);
    this.route = route;
    this.addClickListener(event -> {
      UI.getCurrent().navigate(this.route);
    });
  }


}
