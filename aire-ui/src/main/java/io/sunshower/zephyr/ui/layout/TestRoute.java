package io.sunshower.zephyr.ui.layout;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.WizardPage;
import lombok.val;

@Route(value = "test", layout = MainView.class)
public class TestRoute extends Panel {

  public TestRoute() {
    createContent();
  }

  private void createContent() {
    val wizard = new Wizard<String>();
    wizard.addSteps(Page1.class, Page2.class);
    wizard.setInitialStep(Page1.class);
    wizard.addTransition(Page1.class, Page2.class);
    add(wizard);
  }

  @WizardPage(title = "Second Page", iconFactory = Page2.class, key = "page-2")
  public static class Page2 extends Section implements IconFactory {

    public Page2() {
      add(new Text("Hello"));
    }

    @Override
    public Icon create() {
      return VaadinIcon.LIST.create();
    }
  }

  @WizardPage(title = "First Page", iconFactory = Page1.class, key = "page-1")
  public static class Page1 extends Section implements IconFactory {

    public Page1() {
      add(new Text("World"));

    }

    @Override
    public Icon create() {
      return VaadinIcon.BOLD.create();
    }
  }
}
