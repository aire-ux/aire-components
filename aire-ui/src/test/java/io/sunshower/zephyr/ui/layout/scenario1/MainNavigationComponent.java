package io.sunshower.zephyr.ui.layout.scenario1;

import com.aire.ux.Control;
import com.aire.ux.SelectorMode;
import com.aire.ux.UIExtension;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import java.util.function.Supplier;
import lombok.val;

@UIExtension(
    control =
        @Control(
            target = ":main:navigation",
            selectorMode = SelectorMode.Path,
            factory = MainNavigationComponent.ControlFactory.class))
@Route(value = "main-navigation", layout = ApplicationLayout.class, registerAtStartup = false)
public class MainNavigationComponent extends Main {

  public static class ControlFactory implements Supplier<Button> {

    @Override
    public Button get() {
      val button = new Button("Sup");
      button.addClickListener(
          click -> {
            UI.getCurrent().navigate(MainNavigationComponent.class);
          });
      return button;
    }
  }
}
