package io.sunshower.zephyr.ui.components.scenarios;

import com.vaadin.flow.component.html.Section;
import io.sunshower.zephyr.ui.components.WizardPage;

@WizardPage(key = "page1", title = "Page 1")
public class Page1 extends Section {

  public Page1() {
    addClassName("page-1");
  }
}
