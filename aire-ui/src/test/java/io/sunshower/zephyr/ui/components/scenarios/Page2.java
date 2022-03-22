package io.sunshower.zephyr.ui.components.scenarios;

import com.vaadin.flow.component.html.Section;
import io.sunshower.zephyr.spring.Dynamic;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.WizardModel;
import io.sunshower.zephyr.ui.components.WizardPage;
import java.util.Objects;
import lombok.Getter;

@WizardPage(key = "page2", title = "Page 2")
public class Page2 extends Section {

  @Getter private final Wizard<?> wizard;
  @Getter private final WizardModel<?> model;

  @Dynamic
  public Page2(@Dynamic Wizard<?> parent, @Dynamic WizardModel<?> model) {
    addClassName("page-2");
    this.model = Objects.requireNonNull(model);
    this.wizard = Objects.requireNonNull(parent);
  }
}
