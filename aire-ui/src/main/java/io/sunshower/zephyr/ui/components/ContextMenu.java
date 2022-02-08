package io.sunshower.zephyr.ui.components;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.menubar.MenuBar;
import io.sunshower.zephyr.ui.canvas.Canvas;
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
public class ContextMenu<T> extends HtmlContainer {

  private final Component host;
  private final MenuBar menuBar;
  private final ClientMethod<Serializable> openMethod;


  public ContextMenu(Component host, MenuBar menuBar) {
    this.host = host;
    this.menuBar = menuBar;
    this.openMethod = ClientMethods.withUiSupplier(this).get("open", Location.class);
    getElement().appendChild(menuBar.getElement());
  }

  public ContextMenu(Canvas host) {
    this(host, new MenuBar());
  }


  public MenuBar getMenuBar() {
    return menuBar;
  }

  public void open(ContextMenuEvent<T> event) {
    val element = getElement();
    host.getElement().appendChild(element);
    openMethod.invoke(this, new Location(event.getX(), event.getY()));
  }

  public void close() {
    val element = getElement();
    host.getElement().removeChild(element);
  }

  @RootElement
  static final class Location {

    @Attribute
    double x;
    @Attribute
    double y;

    public Location() {
      x = 0;
      y = 0;
    }

    public Location(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
}
