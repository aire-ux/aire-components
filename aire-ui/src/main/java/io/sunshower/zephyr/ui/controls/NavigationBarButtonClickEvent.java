package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;

@DomEvent("click")
public class NavigationBarButtonClickEvent extends ComponentEvent<NavigationBarButton> {

  public NavigationBarButtonClickEvent(NavigationBarButton source, boolean fromClient) {
    super(source, fromClient);
  }
}
