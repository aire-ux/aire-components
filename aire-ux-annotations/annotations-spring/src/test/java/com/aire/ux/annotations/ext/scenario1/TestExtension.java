package com.aire.ux.annotations.ext.scenario1;

import com.aire.ux.Control;
import com.aire.ux.UIExtension;
import com.aire.ux.annotations.ext.scenario1.TestExtension.ButtonFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Article;
import java.util.function.Supplier;

@UIExtension(control = @Control(
    factory = ButtonFactory.class,
    target = ":test-extension-point:head"
))
public class TestExtension extends Article {

  public static class ButtonFactory implements Supplier<Button> {

    @Override
    public Button get() {
      return null;
    }
  }

}
