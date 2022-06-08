package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;

@Tag("aire-floating-action-button")
@JsModule("./aire/ui/controls/floating-action-button.ts")
@CssImport("./styles/aire/ui/controls/floating-action-button.css")
public class FloatingActionButton extends Component
    implements ClickNotifier<FloatingActionButton>, Focusable<FloatingActionButton>, HasComponents {

  public FloatingActionButton(Icon icon) {
    icon.getElement().setAttribute("slot", "icon");
    add(icon);
  }

  public FloatingActionButton(Image image) {
    image.getElement().setAttribute("slot", "icon");
    add(image);
  }
}
