package io.sunshower.zephyr.ui.layout;

import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Card;
import io.sunshower.zephyr.ui.components.Panel;
import lombok.val;

@Route(value = "test", layout = MainView.class)
public class TestRoute extends Panel {

  public TestRoute() {
    createContent();
  }

  private void createContent() {
    for (int i = 0; i < 100; i++) {
      val card = new Card();
      add(card);
    }
  }
}
