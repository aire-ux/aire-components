package io.sunshower.zephyr.ui.components;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vaadin.flow.component.Component;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class WizardTest {

  private Wizard<Steps> wizard;

  @BeforeEach
  void setUp() {
    wizard = new Wizard<Steps>();
  }

  @Test
  void ensureWizardAPIMakesSenseForLinearWizard() {
    wizard.addStep(Steps.FirstPage, Page.class);
    wizard.addStep(Steps.SecondPage, Page.class);
    wizard.setInitialStep(Steps.FirstPage);
    wizard.addTransition(Steps.FirstPage, Steps.SecondPage);
    val state = wizard.advance();
    assertEquals(state, Steps.SecondPage);
  }

  enum Steps {
    FirstPage,
    SecondPage,
    ThirdPage,
    FourthPage
  }

  static class Page extends Component {

  }

}