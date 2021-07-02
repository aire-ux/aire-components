package com.aire.ux.theme.themescenario;

import com.aire.ux.DefaultTheme;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("scenario")
@DefaultTheme(ScenarioTheme.class)
public class ScenarioView extends VerticalLayout {


  public ScenarioView() {
    add(new TestButton());
  }

}
