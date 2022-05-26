package io.sunshower.zephyr.ui.components.scenarios;

import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.components.Wizard;

@Route("test-wizard")
public class TestWizard extends Article {

  private final Wizard<String, ?> wizard;

  public TestWizard() {
    wizard = new Wizard<>();
    wizard.addSteps(Page1.class, Page2.class, Page3.class);
    wizard.addTransition(Page1.class, Page2.class);
    wizard.addTransition(Page2.class, Page3.class);
    wizard.setInitialStep(Page1.class);
    add(wizard);
  }
}
