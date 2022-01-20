package io.sunshower.zephyr.management;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.Home;
import io.sunshower.zephyr.ui.components.Overlays;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.NavigationBarButton;
import io.sunshower.zephyr.ui.controls.NavigationBarButton.MatchMode;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import io.sunshower.zephyr.ui.layout.ApplicationLayoutDecorator;
import java.util.List;
import lombok.val;

@RoutePrefix("zephyr")
@ParentLayout(ApplicationLayout.class)
public class ZephyrManagementConsoleView extends Panel implements ApplicationLayoutDecorator {

  private NavigationBarButton homeButton;

  public ZephyrManagementConsoleView() {
    Overlays.createHost(this);
  }


  @Override
  public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
    return super.addAttachListener(listener);
  }

  @Override
  public void decorate(ApplicationLayout layout) {
    homeButton = new NavigationBarButton(MainView.class, new Image("images/icon.svg", "Home"));
    homeButton.setClassName("container-end");
    layout.getTop().add(homeButton);

    val homeButton = new NavigationBarButton(ModuleGrid.class,
        List.of("modules/list", "modules/topology"),
        MatchMode.Suffix,
        VaadinIcon.PLUG.create());

    val infoButton = new NavigationBarButton(Home.class, VaadinIcon.HOME.create());

    layout.getNavigation().add(homeButton);
    layout.getNavigation().add(infoButton);
  }

  public void undecorate(ApplicationLayout layout) {
    if (homeButton != null) {
      layout.getTop().remove(homeButton);
    }
  }
}
