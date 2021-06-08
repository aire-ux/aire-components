package com.aire.ux.spring.test.scenario1;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class ChildView extends Div {

  public ChildView() {
    add(new Span("I'm a child"));
  }
}
