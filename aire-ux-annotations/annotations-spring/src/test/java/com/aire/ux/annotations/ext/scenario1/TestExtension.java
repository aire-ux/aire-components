package com.aire.ux.annotations.ext.scenario1;

import com.aire.ux.Control;
import com.aire.ux.UIExtension;
import com.aire.ux.annotations.ext.scenario1.TestExtension.ButtonFactory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.router.Route;
import java.util.function.Supplier;
import lombok.val;

@UIExtension(
    control = @Control(factory = ButtonFactory.class, target = ":test-extension-point:head"))
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
