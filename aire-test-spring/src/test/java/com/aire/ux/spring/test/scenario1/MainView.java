package com.aire.ux.spring.test.scenario1;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.Getter;

@Route("main")
public class MainView extends Main {

  private final Button button;
  private final AtomicInteger count;
  @Getter private final TestService service;

  @Inject
  public MainView(@Nonnull TestService testService) {
    this.service = testService;
    addClassName("main");
    count = new AtomicInteger();
    button = new Button("Waddup");
    button.addClickListener(
        (ComponentEventListener<ClickEvent<Button>>)
            event -> {
              count.incrementAndGet();
            });
    add(button);
  }

  public int getCount() {
    return count.get();
  }
}
