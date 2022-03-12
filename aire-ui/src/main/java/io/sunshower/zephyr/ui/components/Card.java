package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("aire-card")
@JsModule("./aire/ui/components/card.ts")
@CssImport("./styles/aire/ui/components/card.css")
public class Card extends HtmlContainer {

  public void setIcon(Component icon) {
    icon.getElement().setAttribute("slot", "icon");
    add(icon);
  }
}
