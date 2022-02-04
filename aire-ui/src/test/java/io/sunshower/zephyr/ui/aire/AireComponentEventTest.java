package io.sunshower.zephyr.ui.aire;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import elemental.json.Json;
import elemental.json.JsonValue;
import lombok.val;
import org.junit.jupiter.api.Test;

class AireComponentEventTest {

  @Test
  void ensureTypeIsCorrect() {
    val b = Json.parse("{\"name\": \"world\"}");
    assertEquals(B.class,
        new EventType(new A(), true, b).getType());
  }

  @Test
  void ensureValueIsCorrect() {
    val b = Json.parse("{\"name\": \"world\"}");
    val event = new EventType(new A(), true, b);
    assertEquals("world", event.getValue().name);
  }

  @Tag("Sup")
  public static class A extends Component {

  }

  @RootElement
  public static class B {

    @Attribute
    String name;

    public B() {

    }
  }

  class EventType extends AireComponentEvent<A, B> {

    protected EventType(A source, boolean fromClient, JsonValue value) {
      super(source, fromClient, value);
    }
  }

}