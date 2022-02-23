package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.listeners.Location;
import io.sunshower.zephyr.ui.controls.MenuForest;
import io.sunshower.zephyr.ui.rmi.ClientMethod;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import java.io.Serializable;
import lombok.val;

/**
 * vaadin's context-menu doesn't really work properly with the canvas component--there's no way to
 * distinguish between sub-elements, etc.
 */
@Tag("aire-context-menu")
@JsModule("./aire/ui/components/context-menu.ts")
@CssImport("./styles/aire/ui/components/context-menu.css")
public class ContextMenu<T> extends HtmlContainer
    implements ComponentEventListener<ContextMenuStateChangedEvent> {

  private final Component host;
  private final MenuForest menuForest;
  private final ClientMethod<Serializable> openMethod;

  public ContextMenu(Component host, MenuForest menuForest) {
    this.host = host;
    this.menuForest = menuForest;
    this.openMethod = ClientMethods.withUiSupplier(this).get("open", Location.class);
  }

  public ContextMenu(Canvas host) {
    this(host, new MenuForest());
    addContextMenuStateChangedEventListener(this);
  }

  public Registration addContextMenuStateChangedEventListener(
      ComponentEventListener<ContextMenuStateChangedEvent> listener) {
    return addListener(ContextMenuStateChangedEvent.class, listener);
  }

  public MenuForest getMenu() {
    return menuForest;
  }

  public void open(ContextMenuEvent<T> event) {
    val element = getElement();
    val location = event.getLocation();
    openMethod.invoke(this, location);
    host.getElement().appendChild(element);
    configureMenubar(menuForest);
  }

  public void close() {
    val element = getElement();
    host.getElement().removeChild(element);
  }

  protected void configureMenubar(MenuForest menuForest) {
    configureMenubarElement(menuForest);
    getElement().setAttribute("slot", "context-menu");
    getElement().appendChild(menuForest.getElement());
  }

  private void configureMenubarElement(MenuForest menuForest) {
    val mbel = menuForest.getElement();
    mbel.setAttribute("theme", "menu-vertical");
    mbel.setAttribute("exportparts", "container");
  }

  @Override
  public void onComponentEvent(ContextMenuStateChangedEvent event) {
    if (event.isClosed()) {
      close();
    }
  }
}
