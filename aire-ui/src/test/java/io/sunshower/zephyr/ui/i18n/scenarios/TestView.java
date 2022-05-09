package io.sunshower.zephyr.ui.i18n.scenarios;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.i18n.Localize;
import io.sunshower.zephyr.ui.i18n.Localized;

@Localize
@Route("test-view")
public class TestView extends VerticalLayout {

  @Localized("name")
  private String name;

  @Localized private TextField textField;

  public TestView() {
    textField = new TextField();
    add(textField);
  }

  public TextField getTextField() {
    return textField;
  }

  public String getName() {
    return name;
  }
}
