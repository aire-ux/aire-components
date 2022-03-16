package io.sunshower.zephyr.ui.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.di.Instantiator;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WizardTest {

  private Wizard<String> wizard;
  private Instantiator instantiator;

  @BeforeEach
  void setUp() {
    instantiator = mock(Instantiator.class);
    wizard =
        new Wizard<>() {
          @Override
          protected Instantiator getInstantiator() {
            return instantiator;
          }
        };
  }

  @Test
  void ensureAddingStepWorks() {
    val p1 =
        Wizard.key(Steps.FirstPage)
            .title("just a title")
            .page(Page.class)
            .icon(VaadinIcon.LIST::create);

    val p2 =
        Wizard.key(Steps.FirstPage)
            .title("just a title")
            .page(Page.class)
            .icon(VaadinIcon.LIST::create);

    val w = new Wizard<Steps>();
    w.addSteps(p1, p2);
    w.setInitialStep(Steps.FirstPage);
    w.addTransition(Steps.FirstPage, Steps.SecondPage);

    val next = w.advance();
    assertEquals(Steps.SecondPage, next);
  }

  @Test
  void ensureWizardAPIMakesSenseForLinearWizard() {
    wizard.addStep(Page.class);
    wizard.addStep(Page2.class);
    wizard.setInitialStep(Page.class);
    wizard.addTransition(Page.class, Page2.class);
    val state = wizard.advance();
    assertEquals(state, "hello2");
  }

  enum Steps {
    FirstPage,
    SecondPage,
    ThirdPage,
    FourthPage
  }

  @WizardPage(key = "hello2", title = "hello")
  static class Page2 extends Component {}

  @WizardPage(key = "hello", title = "hello")
  static class Page extends Component {}
}
