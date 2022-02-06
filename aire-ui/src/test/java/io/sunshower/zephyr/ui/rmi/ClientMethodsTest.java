package io.sunshower.zephyr.ui.rmi;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexTemplateAction;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

@AireUITest
class ClientMethodsTest {

  @Test
  @SneakyThrows
  void ensureAddVertexTemplateActionsConstructorIsLocatable() {
    AddVertexTemplateAction.class.getDeclaredConstructor(Supplier.class, VertexTemplate.class);
  }

  @ViewTest
  void ensureClientMethodIsInvokable() {
    val result =
        ClientMethods.withUiSupplier(UI::getCurrent)
            .construct(AddVertexTemplateAction.class, new VertexTemplate());
    assertNotNull(result);
  }
}
