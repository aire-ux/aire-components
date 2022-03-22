package io.sunshower.zephyr.ui.layout;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.spring.Dynamic;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.WizardPage;
import io.sunshower.zephyr.ui.layout.TestRoute.Page1.IF;
import io.sunshower.zephyr.ui.layout.TestRoute.Page2.IF2;
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


  @WizardPage(title = "Third Page", iconFactory = IF2.class, key = "page-3")
  public static class Page3 extends Section implements IconFactory {

    private final Wizard<String> wizard;

    public static class IF2 implements IconFactory {

      @Override
      public Icon create() {
        return VaadinIcon.UNDERLINE.create();
      }
    }

    @Dynamic
    public Page3(@Dynamic Wizard<String> wizard) {
      this.wizard = wizard;
      add(new Text("page3"));

      val nextButton = new Button("next");
      val previousButton = new Button("previous");

      nextButton.addClickListener(
          click -> {
            wizard.advance();
          });

      previousButton.addClickListener(
          click -> {
            wizard.retreat();
          });
      add(nextButton);
      add(previousButton);
    }

    @Override
    public Icon create() {
      return VaadinIcon.LIST.create();
    }
  }

  @WizardPage(title = "Second Page", iconFactory = IF2.class, key = "page-2")
  public static class Page2 extends Section implements IconFactory {

    private final Wizard<String> wizard;

    public static class IF2 implements IconFactory {

      @Override
      public Icon create() {
        return VaadinIcon.UNDERLINE.create();
      }
    }

    @Dynamic
    public Page2(@Dynamic Wizard<String> wizard) {
      this.wizard = wizard;
      add(new Text("Hello2"));
      val nextButton = new Button("next");
      val previousButton = new Button("previous");

      nextButton.addClickListener(
          click -> {
            wizard.advance();
          });

      previousButton.addClickListener(
          click -> {
            wizard.retreat();
          });
      add(nextButton);
      add(previousButton);
    }

    @Override
    public Icon create() {
      return VaadinIcon.LIST.create();
    }
  }

  @WizardPage(title = "First Page", iconFactory = IF.class, key = "page-1")
  public static class Page1 extends Section implements IconFactory {

    private final Wizard<String> wizard;

    public static class IF implements IconFactory {

      @Override
      public Icon create() {
        return VaadinIcon.BOLD.create();
      }
    }

    @Dynamic
    public Page1(@Dynamic Wizard<String> wizard) {
      this.wizard = wizard;
      add(new Text("World"));
      val nextButton = new Button("next");
      val previousButton = new Button("previous");

      nextButton.addClickListener(
          click -> {
            wizard.advance();
          });

      previousButton.addClickListener(
          click -> {
            wizard.retreat();
          });
      add(nextButton);
      add(previousButton);
    }

    @Override
    public Icon create() {
      return VaadinIcon.BOLD.create();
    }
  }
}
