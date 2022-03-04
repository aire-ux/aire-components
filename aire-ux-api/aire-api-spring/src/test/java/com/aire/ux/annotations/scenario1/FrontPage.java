package com.aire.ux.annotations.scenario1;

import com.aire.ux.Host;
import com.aire.ux.Slot;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.Objects;
import javax.inject.Inject;

@Host("my-id")
@Route("front-page")
public class FrontPage extends VerticalLayout {

  public final Coolbean bean;

  @Inject
  public FrontPage(Coolbean bean) {
    this.bean = Objects.requireNonNull(bean);
  }

  @Slot("main")
  void mainContent(Component component) {
    this.add(component);
  }
}
