package io.sunshower.zephyr.ui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

import com.aire.ux.DefaultComponentExtension;
import com.aire.ux.Extensions;
import com.aire.ux.Selection;
import com.aire.ux.UserInterface;
import com.aire.ux.actions.ActionEvent.Type;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.actions.Actions;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
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
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@AireUITest
@Navigate("")
@Routes(scanClassPackage = MainView.class)
class ApplicationLayoutTest {

  @BeforeEach
  void setUp(@Context TestContext $) {
    val button = $.selectFirst("vaadin-button[text~=Hello]", Button.class);
    assertTrue(button.isEmpty());
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
    assertTrue(result.isPresent());
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
  @DirtiesContext
  void ensureRegisteringSimpleComponentWorks(@Autowired UserInterface ui, @Context TestContext $) {
    val extension =
        new DefaultComponentExtension<>(
            ":management-menu",
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
    $.flush();
    assertEquals(0, ui.getExtensionRegistry().getExtensionCount());
  }

  @ViewTest
  @DirtiesContext
  void ensureActionManagerCanEnableAndDisableButtons(
      @Autowired UserInterface ui, @Context TestContext $, @Autowired ActionManager actionManager) {
    val action = spy(Actions.create("ui.module.stop", (self) -> {

    }));
    ui.register(Selection.path(":main:navigation"),
        Extensions.create(":management-menu", (NavigationBar p) -> {
          val button = new Button("hello");
          action.addActionEventListener(Type.ActionEnabled, (eventType, event) -> {
            button.setEnabled(true);
          });
          action.addActionEventListener(Type.ActionDisabled, (eventType, event) -> {
            button.setEnabled(true);
          });
          p.add(button);
        }));
    action.enable();
    $.flush();
    assertTrue($.selectFirst("vaadin-button[enabled]", Button.class).isPresent());
    action.dispose();

  }
}
