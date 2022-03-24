package io.sunshower.zephyr.ui.layout;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.AbstractWizardPage;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.WizardPage;
import io.sunshower.zephyr.ui.layout.TestRoute.Page2.IF1;
import lombok.val;

@Route(value = "test", layout = MainView.class)
public class TestRoute extends Panel {

  public TestRoute() {
    createContent();
  }

  private void createContent() {
    val wizard = new Wizard<String>();
    wizard.addSteps(Page1.class, Page2.class, Page3.class);
    wizard.setInitialStep(Page1.class);
    wizard.addTransition(Page1.class, Page2.class);
    wizard.addTransition(Page2.class, Page3.class);
    add(wizard);
  }

  public static class Person {}

  public static class Address {}

  @WizardPage(title = "Info2", key = "page-3")
  public static class Page3 extends AbstractWizardPage<String, Person> implements IconFactory {

    public Page3() {
      super(Person.class);
    }

    @Override
    public Icon create() {
      return VaadinIcon.ARCHIVES.create();
    }
  }

  @WizardPage(title = "Info", key = "page-1")
  public static class Page1 extends AbstractWizardPage<String, Person> implements IconFactory {

    public Page1() {
      super(Person.class);
    }

    @Override
    public Icon create() {
      return VaadinIcon.ARCHIVES.create();
    }
  }

  @WizardPage(title = "Addresses", iconFactory = IF1.class, key = "page-2")
  public static class Page2 extends AbstractWizardPage<String, Address> {

    public Page2() {
      super(Address.class);
    }

    public static class IF1 implements IconFactory {

      @Override
      public Icon create() {
        return VaadinIcon.BOLD.create();
      }
    }
  }
}
