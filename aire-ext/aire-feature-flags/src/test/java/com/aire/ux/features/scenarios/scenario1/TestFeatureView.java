package com.aire.ux.features.scenarios.scenario1;

import com.aire.ux.RouteDefinition.Scope;
import com.aire.ux.RouteExtension;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.management.PluginTabView;
import io.sunshower.zephyr.ui.controls.Breadcrumb;

@RouteExtension(scopes = Scope.Global)
@Breadcrumb(name = "Test Feature", icon = "vaadin:plug", host = MainView.class)
@Route(value = "modules/test", layout = PluginTabView.class, registerAtStartup = false)
public class TestFeatureView extends Main {

  public TestFeatureView() {
    add(new Button("test"));
  }
}
