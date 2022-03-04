package com.aire.ux.annotations.ext.scenario1;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.router.Route;
import java.util.function.Supplier;
import lombok.val;

@Route(value = "test-extension", registerAtStartup = false)
public class TestExtension extends Article {

  public static class ButtonFactory implements Supplier<Button> {

    @Override
    public Button get() {
      val button = new Button("hello");
      button.addClickListener(
          click -> {
            UI.getCurrent().navigate(TestExtension.class);
          });
      return button;
    }
  }
}
