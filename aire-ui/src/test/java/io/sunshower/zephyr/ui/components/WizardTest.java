package io.sunshower.zephyr.ui.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.Command;
import io.sunshower.arcus.reflect.Reflect;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WizardTest {

  private Wizard<String, ?> wizard;
  private Instantiator instantiator;

  @BeforeEach
  void setUp() {
    instantiator = mock(Instantiator.class);
    doAnswer(
        invocationOnMock -> {
          val type = invocationOnMock.getArgument(0);
          return Reflect.instantiate((Class) type);
        })
        .when(instantiator)
        .createComponent(any());
    wizard = create();
  }

  @Test
  void ensureAddingStepWorks() {
    val p1 =
        Wizard.key(Steps.FirstPage).title("just a title").page(Page.class).icon(VaadinIcon.LIST);

    val p2 =
        Wizard.key(Steps.SecondPage).title("just a title").page(Page.class).icon(VaadinIcon.LIST);

    val w = this.<Steps>create();
    w.addSteps(p1, p2);
    w.setInitialStep(Steps.FirstPage);
    w.addTransition(Steps.FirstPage, Steps.SecondPage);

    w.onAttach(null);
    val next = w.advance();
    assertEquals(Steps.SecondPage, next);
  }

  @Test
  void ensureWizardAPIMakesSenseForLinearWizard() {
    wizard.addStep(Page.class);
    wizard.addStep(Page2.class);
    wizard.setInitialStep(Page.class);
    wizard.addTransition(Page.class, Page2.class);
    wizard.onAttach(null);
    val state = wizard.advance();
    assertEquals(state, "hello2");
  }

  <K> Wizard<K, ?> create() {
    final Wizard<K, ?> wizard =
        new Wizard<>() {
          @Override
          @SuppressWarnings("unchecked")
          protected <T> T instantiate(Class<T> type) {
            return (T) instantiator.createComponent((Class<Component>) type);
          }

          @Override
          protected void access(Command command) {
            command.execute();
          }
        };
    return wizard;
  }

  enum Steps {
    FirstPage,
    SecondPage,
    ThirdPage,
    FourthPage
  }

  @WizardPage(key = "hello2", title = "hello")
  public static class Page2 extends Section {

  }

  @WizardPage(key = "hello", title = "hello")
  public static class Page extends Section {

  }
}
