package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import java.util.Locale;
import lombok.NonNull;

@Tag("aire-split-panel")
@JsModule("./aire/ui/components/split-panel.ts")
@CssImport("./styles/aire/ui/components/split-panel.css")
public class SplitPanel extends Component implements HasComponents, HasStyle {

  static final PropertyDescriptor<String, String> TYPE_PROPERTY_DESCRIPTOR;

  static {
    TYPE_PROPERTY_DESCRIPTOR = PropertyDescriptors.attributeWithDefault("type", "horizontal");
  }

  /**
   * the "first" component in this split-panel. If this is arranged horizontally, then this is the
   * left-hand side. If vertical, then this is the top
   */
  private Component first;

  /** the "second" component. Right/horizontal, bottom/vertical */
  private Component second;

  public SplitPanel() {
    this(Type.Horizontal);
  }

  public SplitPanel(@NonNull Type type) {
    TYPE_PROPERTY_DESCRIPTOR.set(this, type.name().toLowerCase(Locale.ROOT));
  }

  public Component getFirst() {
    return first;
  }

  public void setFirst(@NonNull Component component) {
    component.getElement().setAttribute("slot", "first");
    add(component);
    this.first = component;
  }

  public Component getSecond() {
    return second;
  }

  public void setSecond(@NonNull Component component) {
    component.getElement().setAttribute("slot", "second");
    add(component);
    this.second = component;
  }

  public enum Type {
    Horizontal,
    Vertical
  }
}
