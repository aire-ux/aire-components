package com.aire.ux.annotations.ext.scenario1;

import com.aire.ux.Host;
import com.aire.ux.Slot;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Host("test-extension-point")
public class TestExtensionPoint extends VerticalLayout {

  @Slot(":head")
  private Header header;

  public TestExtensionPoint() {
    header = new Header();
    add(header);
  }


}
