package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.aire.ux.condensation.Condensation;
import com.aire.ux.condensation.DocumentWriter;
import com.aire.ux.test.Context;
import com.aire.ux.test.Context.Mode;
import com.aire.ux.test.View;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexTemplateAction;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import java.io.IOException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;

@AireUITest
@SpyBean(Canvas.class)
class CanvasTest {

  private DocumentWriter writer;
  private Condensation condensation;

  @BeforeEach
  void setUp() {
    condensation = Condensation.create("json");
    writer = condensation.getWriter();
  }

  @ViewTest
  void ensureCanvasIsConstructable(@View Canvas canvas) {
    assertNotNull(canvas);
  }

  @ViewTest
  void ensureUIIsSpied(@Context(mode = Mode.Spy) UI ui) {
    assertTrue(Mockito.mockingDetails(ui).isSpy());
  }

  @ViewTest
  void ensureCanvasInvocationAPIMakesSense(@View Canvas canvas, @Context(mode = Mode.Spy) UI ui)
      throws IOException {
    val result = VertexTemplate.newBuilder("test").width(100f).height(100f).create();

    val method =
        ClientMethods.withUiSupplier(() -> ui).get("addVertexTemplate", VertexTemplate.class);
    val spiedElement = spy(canvas.getElement());
    doReturn(spiedElement).when(canvas).getElement();

    method.invoke(canvas, result);
    verify(canvas, times(2)).getElement();
    verify(spiedElement)
        .callJsFunction(eq("addVertexTemplate"), eq(writer.write(VertexTemplate.class, result)));
    Mockito.reset(ui);
  }

  @ViewTest
  void ensureInvocationAPIResultsInActionAvailableWhenAppliedThroughAction(
      @View Canvas canvas, @Context(mode = Mode.Spy) UI ui) {
    val result = VertexTemplate.newBuilder("test").width(100f).height(100f).create();

    val action = new AddVertexTemplateAction(() -> ui, result);
    action.apply(canvas.getModel());

  }

}
