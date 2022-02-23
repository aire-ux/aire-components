package io.sunshower.zephyr.ui.canvas.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.View;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.canvas.AbstractCanvasTest;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.Edge;
import io.sunshower.zephyr.ui.canvas.Vertex;
import java.util.List;
import lombok.val;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

@AireUITest
@SpyBean(Canvas.class)
class AddVerticesCanvasActionTest extends AbstractCanvasTest {

  @ViewTest
  @DirtiesContext
  void ensureConnectingNodesWorks(@View Canvas canvas) {
    val source = new Vertex();
    val target = new Vertex();
    canvas.invoke(AddVerticesAction.class, List.of(source, target));
    assertEquals(2, canvas.getModel().getVertices().size());
    assertTrue(canvas.getModel().getEdges().isEmpty());
    canvas.invoke(ConnectVerticesAction.class, List.of(new Edge(source.getId(), target.getId())));
    assertEquals(1, canvas.getModel().getEdges().size());
  }
}
