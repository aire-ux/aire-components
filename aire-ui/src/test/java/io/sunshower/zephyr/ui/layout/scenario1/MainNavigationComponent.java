package io.sunshower.zephyr.ui.layout.scenario1;

import com.aire.ux.Control;
import com.aire.ux.SelectorMode;
import com.aire.ux.UIExtension;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import java.util.function.Supplier;

@UIExtension(
    control =
        @Control(
            target = ":main:navigation",
            selectorMode = SelectorMode.Path,
            factory = MainNavigationComponent.ControlFactory.class))
public class MainNavigationComponent extends Main {

  public static class ControlFactory implements Supplier<Button> {

    @Override
    public Button get() {
      return null;
    }
  }
}
