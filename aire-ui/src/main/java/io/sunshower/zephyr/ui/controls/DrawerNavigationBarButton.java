package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.ui.components.Drawer;

@Tag("aire-navigation-bar-button")
@JsModule("./aire/ui/controls/navigation-bar-button.ts")
@CssImport("./styles/aire/ui/controls/navigation-bar-button.css")
public class DrawerNavigationBarButton extends HtmlContainer implements
    ClickNotifier<DrawerNavigationBarButton>,
    Focusable<DrawerNavigationBarButton> {

  private final Icon icon;
  private final String text;
  private final Drawer host;
  private final Class<? extends HasElement> component;
  private final Registration clickRegistration;
  private final Registration drawerOpenedRegistration;
  private final Registration drawerClosedRegistration;
  private Component contents;


  public DrawerNavigationBarButton(
      Icon icon,
      String text,
      Drawer host,
      Class<? extends HasElement> component
  ) {
    this.icon = icon;
    this.text = text;
    this.host = host;
    this.component = component;
    add(icon);
    add(text);
    addClassName("aire-drawer-button");
    getElement().setAttribute("rotate", "true");

    drawerOpenedRegistration = host.addDrawerOpenedEventListener(drawerOpened -> {
      getUI().map(Instantiator::get)
          .ifPresent(instantiator -> {
            this.contents = (Component) instantiator.getOrCreate(component);
            host.add(contents);
          });
    });

    drawerClosedRegistration = host.addDrawerClosedEventListener(drawerClosedEvent -> {
      if (contents != null) {
        host.remove(contents);
      }
    });

    clickRegistration = addClickListener(buttonClicked -> {
      host.toggle();
    });
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    clickRegistration.remove();
    drawerClosedRegistration.remove();
    drawerOpenedRegistration.remove();
  }
}
