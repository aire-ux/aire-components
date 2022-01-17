package io.sunshower.zephyr.ui.layout;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Nav;
import io.sunshower.zephyr.ui.navigation.NavigationBar;
import java.util.Objects;
import lombok.val;


@Tag("aire-application-layout")
@JsModule("./aire/ui/layout/application-layout.ts")
@CssImport("./styles/aire/ui/layout/application-layout.css")
public class ApplicationLayout extends Main {


  /**
   *
   */
  private HasComponents top;
  private HasComponents bottom;
  private HasComponents content;
  private HasComponents navigation;

  public ApplicationLayout() {
    top = createTop();
    setTop(top);

    navigation = createNavigation();
    setNavigation(navigation);

    bottom = createBottom();
    setBottom(bottom);
  }

  public void setBottom(HasComponents bottom) {
    set(Slot.Bottom, bottom);
  }

  /**
   * @param top the component to add to the {@code top} slot
   */
  public void setTop(HasComponents top) {
    set(Slot.Top, top);
  }


  /**
   *
   * @param navigation the component to add to the {@code navigation} slot
   */
  public void setNavigation(HasComponents navigation) {
    set(Slot.Navigation, navigation);
  }


  @SuppressWarnings("unchecked")
  public <T extends HasElement, U extends HasElement> U set(Slot slot, T value) {
    Objects.requireNonNull(slot, "Slot must not be null!");
    Objects.requireNonNull(value, "value must not be null!");

    val existing = value.getElement().getChildren()
        .filter(child -> slot.slot.equals(child.getAttribute("slot")))
        .findAny();
    existing.ifPresent(child -> getElement().removeChild(child));
    value.getElement().setAttribute("slot", slot.slot);
    getElement().appendChild(value.getElement());
    switch (slot) {
      case Top:
        this.top = (HasComponents) value;
        break;
      case Bottom:
        this.bottom = (HasComponents) value;
        break;
      case Navigation:
        this.navigation = (HasComponents) value;
        break;
      case Content:
        this.content = (HasComponents) value;
      default:
        throw new IllegalArgumentException(
            "Not sure how you got slot '" + slot + "', but it's not one of mine");
    }
    return (U) existing.orElse(null);
  }

  protected HasComponents createTop() {
    val topNav = new NavigationBar();
    return topNav;
  }


  protected HasComponents createBottom() {
    val bottom = new NavigationBar();
    return bottom;
  }


  protected HasComponents createNavigation() {
    val navigation = new NavigationBar();
    return navigation;
  }


  public enum Slot {

    /**
     * top-slot
     */
    Top("top"),
    Bottom("bottom"),
    Content("content"),
    Navigation("navigation");


    final String slot;

    Slot(String slot) {
      this.slot = Objects.requireNonNull(slot);
    }


  }
}
