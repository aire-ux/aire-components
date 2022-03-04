package io.zephyr.tutorials.primarycomponent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.controls.NavigationBarButton;
import io.sunshower.zephyr.ui.controls.NavigationBarButton.MatchMode;
import java.util.List;
import java.util.function.Supplier;
import lombok.val;

@Route(value = "main-navigation", layout = MainView.class, registerAtStartup = false)
public class TestComponent extends Div {

  public TestComponent() {
    add(new H1("Hello from a module!"));
  }

  public static class ControlFactory implements Supplier<Component> {

    @Override
    public Component get() {
      val homeButton =
          new NavigationBarButton(
              TestComponent.class,
              List.of("main-navigation"),
              MatchMode.Suffix,
              VaadinIcon.SERVER.create());
      return homeButton;
    }
  }
}
