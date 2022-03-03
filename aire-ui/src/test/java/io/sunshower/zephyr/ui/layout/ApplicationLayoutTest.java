package io.sunshower.zephyr.ui.layout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

import com.aire.ux.DefaultComponentExtension;
import com.aire.ux.Selection;
import com.aire.ux.UserInterface;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.RegisterExtension;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.button.Button;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.layout.scenario1.MainNavigationComponent;
import io.sunshower.zephyr.ui.navigation.NavigationBar;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

@AireUITest
@Navigate("")
@Routes(scanClassPackage = MainView.class)
class ApplicationLayoutTest {

  @ViewTest
  void ensureButtonIsInjectable(
      @Select("vaadin-button[text=Sup]") Button button, @Context TestContext $) {
    assertNotNull(button);
    assertTrue($.select(MainNavigationComponent.class).isEmpty());
    button.click();
    assertFalse($.select(MainNavigationComponent.class).isEmpty());
  }

  @ViewTest
  void ensureMainNavigationIsNotSelectable(@Context TestContext $) {
    assertFalse($.selectFirst(MainNavigationComponent.class).isPresent());
  }

  @ViewTest
  void ensureUserInterfaceIsInjectable(@Autowired UserInterface ui) {
    assertNotNull(ui);
  }

  @ViewTest
  void ensureSelectionWorks(@Autowired UserInterface ui) {
    val result = ui.selectFirst(Selection.path(":main:navigation", MainView.class));
    assertTrue(result.isPresent());
    assertInstanceOf(NavigationBar.class, result.get());
  }

  @ViewTest
  void ensureSelectionWorksWithNoAuxClass(@Autowired UserInterface ui) {
    val result = ui.selectFirst(Selection.path(":main:navigation"));
  }

  @ViewTest
  void ensureCssWorks(@Select("aire-application-layout") ApplicationLayout layout) {
    assertNotNull(layout);
  }

  @ViewTest
  void ensurePathsWork(@Select(mode = "path", value = ":main") ApplicationLayout layout) {
    assertNotNull(layout);
  }

  @ViewTest
  void ensurePathsWorkOnSubPath(
      @Select(mode = "path", value = ":main:navigation") NavigationBar layout) {
    assertNotNull(layout);
  }

  @ViewTest
  void ensureRegisteringSimpleComponentWorks(
      @Autowired UserInterface ui,
      @Context TestContext $
  ) {
    val extension = new DefaultComponentExtension<>(":management-menu",
        (NavigationBar parent) -> {
          val button = spy(new Button("Hello"));
          parent.add(button);
        });
    var button = $.selectFirst("vaadin-button[text~=Hello]", Button.class);
    assertFalse(button.isPresent());
    val registration = ui.register(Selection.path(":main:navigation"), extension);
    $.flush();
    val menu = ui.selectFirst(Selection.path(":main:navigation:management-menu"));
    assertNotNull(menu);
    button = $.selectFirst("vaadin-button[text~=Hello]", Button.class);
    assertTrue(button.isPresent());
    registration.close();
  }

}
