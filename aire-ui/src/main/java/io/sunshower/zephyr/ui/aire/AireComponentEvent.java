package io.sunshower.zephyr.ui.aire;

import static io.sunshower.zephyr.condensation.CondensationUtilities.wrap;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import elemental.json.JsonValue;
import java.lang.reflect.ParameterizedType;
import lombok.Getter;

public class AireComponentEvent<T extends Component, U> extends ComponentEvent<T> {

  @Getter
  private final U value;

  @Getter
  private final Class<U> type;


  protected AireComponentEvent(Class<U> type, T source, boolean fromClient, JsonValue value) {
    super(source, fromClient);
    this.type = type;
    this.value = wrap(type, value);
  }

  protected AireComponentEvent(T source, boolean fromClient, JsonValue value) {
    super(source, fromClient);
    this.type = extractType();
    this.value = wrap(type, value);
  }


  @SuppressWarnings("unchecked")
  private Class<U> extractType() {
    return (Class<U>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
  }


}
