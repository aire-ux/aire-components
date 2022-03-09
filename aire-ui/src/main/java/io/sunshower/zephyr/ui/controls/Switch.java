package io.sunshower.zephyr.ui.controls;

import static com.vaadin.flow.component.PropertyDescriptors.attributeWithDefault;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.shared.Registration;
import java.util.Locale;
import lombok.val;

@Tag("aire-switch")
@JsModule("./aire/ui/controls/switch.ts")
@CssImport("./styles/aire/ui/controls/switch.css")
public class Switch extends Input {

  static final PropertyDescriptor<String, String> MODE_PROPERTY_DESCRIPTOR;
  static final PropertyDescriptor<String, String> LABEL_PROPERTY_DESCRIPTOR;

  static final PropertyDescriptor<String, String> DIRECTION_PROPERTY_DESCRIPTOR;

  static {
    MODE_PROPERTY_DESCRIPTOR = attributeWithDefault("mode", propertyValue(Mode.Enabled));

    DIRECTION_PROPERTY_DESCRIPTOR =
        attributeWithDefault("direction", propertyValue(Direction.Horizontal));

    LABEL_PROPERTY_DESCRIPTOR = attributeWithDefault("label", "");
  }

  public Switch() {
    this("");
  }

  public Switch(String label) {
    this(new Text(label));
  }

  public Switch(Component label) {
    getElement().appendChild(label.getElement());
    setMode(Mode.Enabled);
  }

  public void setEnabled(boolean enabled) {
    if(enabled) {
      setMode(Mode.Enabled);
    } else {
      setMode(Mode.Disabled);
    }
  }

  static String propertyValue(Enum<?> e) {
    return e.name().toLowerCase(Locale.ROOT);
  }

  static <T extends Enum<T>> T parse(Class<T> e, T defaultValue, String value) {
    val constants = e.getEnumConstants();
    for (val constant : constants) {
      if (constant.name().equalsIgnoreCase(value)) {
        return constant;
      }
    }
    return defaultValue;
  }

  public Registration addSelectionChangeListener(
      ComponentEventListener<SwitchStateChangedEvent> listener) {
    return addListener(SwitchStateChangedEvent.class, listener);
  }

  public Direction getDirection() {
    return parse(Direction.class, Direction.Horizontal, get(DIRECTION_PROPERTY_DESCRIPTOR));
  }

  public void setDirection(Direction direction) {
    set(DIRECTION_PROPERTY_DESCRIPTOR, propertyValue(direction));
  }

  public Mode getMode() {
    return parse(Mode.class, Mode.Enabled, get(MODE_PROPERTY_DESCRIPTOR));
  }

  public void setMode(Mode mode) {
    if(mode == Mode.Enabled) {
      super.setEnabled(true);
    } else if(mode == Mode.Disabled) {
      super.setEnabled(false);
    }
    set(MODE_PROPERTY_DESCRIPTOR, propertyValue(mode));
  }

  public enum Mode {
    Enabled,
    Disabled,
    Indeterminate
  }

  public enum Direction {
    Vertical,
    Horizontal;
  }
}
