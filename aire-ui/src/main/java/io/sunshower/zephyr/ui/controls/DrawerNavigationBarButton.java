package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.ui.components.Drawer;
import java.util.function.Supplier;

@Tag("aire-navigation-bar-button")
@JsModule("./aire/ui/controls/navigation-bar-button.ts")
@CssImport("./styles/aire/ui/controls/navigation-bar-button.css")
public class DrawerNavigationBarButton extends HtmlContainer implements
    ClickNotifier<DrawerNavigationBarButton>,
    Focusable<DrawerNavigationBarButton> {

  /**
   * the icon for this button
   */
  private final Icon icon;

  /**
   * immutable state
   */
  /**
   * the text for this buttoin
   */
  private final String text;
  /**
   * this host for this button
   */
  private final Drawer host;
  private final Registration clickRegistration;
  private final Registration drawerOpenedRegistration;
  private final Registration drawerClosedRegistration;
  private final Supplier<? extends Component> componentSupplier;
  /**
   * mutable state
   */
  private Component contents;

  public DrawerNavigationBarButton(
      Icon icon,
      String text,
      Drawer host,
      Class<? extends Component> type
  ) {
    this(icon, text, host, () -> Instantiator.get(UI.getCurrent()).createComponent(type));
  }


  public DrawerNavigationBarButton(
      Icon icon,
      String text,
      Drawer host,
      Supplier<? extends Component> componentSupplier
  ) {
    this.icon = icon;
    this.text = text;
    this.host = host;
    this.componentSupplier = componentSupplier;
    add(icon);
    add(text);
    addClassName("aire-drawer-button");
    getElement().setAttribute("rotate", "true");

    drawerOpenedRegistration = host.addDrawerOpenedEventListener(drawerOpened -> {
      host.add(contents = componentSupplier.get());
    });

    drawerClosedRegistration = host.addDrawerClosedEventListener(drawerClosed -> {
      host.removeAll();
    });

    clickRegistration = addClickListener(buttonClicked -> {
      if (host.isOpen() && contents != null) {
        host.remove(contents);
      }
      host.toggle();
    });
  }

  private boolean hasChild(Drawer host, Component contents) {
    return host.getChildren().anyMatch(contents::equals);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    clickRegistration.remove();
    drawerOpenedRegistration.remove();
  }


  public void click() {
    this.fireEvent(
        new ClickEvent<DrawerNavigationBarButton>(this, false, 0, 0, 0, 0, 0, 0, false, false,
            false, false));
  }
}
