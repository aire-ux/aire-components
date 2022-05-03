package io.sunshower.zephyr.ui.components.scenarios;

import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.textfield.TextField;
import io.sunshower.zephyr.spring.Dynamic;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.WizardModel;
import io.sunshower.zephyr.ui.components.WizardPage;
import java.util.Objects;
import lombok.Getter;

@WizardPage(key = "page2", title = "Page 2")
public class Page2 extends Section {

  private TextField textField;

  @Dynamic
  public Page2() {
    addClassName("page-2");
    configure();
  }

  private void configure() {
    textField = new TextField();
    textField.getElement().getClassList().add("text-field");
    add(textField);
  }
}
