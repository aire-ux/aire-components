package io.sunshower.zephyr.management;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;

@Route(value = "modules/list", layout = PluginTabView.class)
public class ModuleGrid extends Div {

  public ModuleGrid() {
    add(new Button("Module Grid"));
  }

}
