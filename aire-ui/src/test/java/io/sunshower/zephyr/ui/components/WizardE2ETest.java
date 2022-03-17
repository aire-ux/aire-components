package io.sunshower.zephyr.ui.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.components.scenarios.Page1;
import io.sunshower.zephyr.ui.components.scenarios.Page2;
import io.sunshower.zephyr.ui.components.scenarios.Page3;
import io.sunshower.zephyr.ui.components.scenarios.TestWizard;
import lombok.val;
import org.springframework.test.annotation.DirtiesContext;

@AireUITest
@DirtiesContext
@Routes(scanClassPackage = TestWizard.class)
public class WizardE2ETest {

  @ViewTest
  @Navigate("test-wizard")
  void ensureWizardIsAvailable(@Select("aire-wizard") Wizard<?> wizard, @Context TestContext $) {
    assertNotNull(wizard);
    val r = $.selectFirst("section.page-1", Page1.class);
    assertTrue(r.isPresent());
  }

  @ViewTest
  @Navigate("test-wizard")
  void ensureNavigatingWorks(@Select("aire-wizard") Wizard<?> wizard, @Context TestContext $) {
    assertNotNull(wizard);
    wizard.advance();
    val r = $.selectFirst("section.page-2", Page2.class);
    assertTrue(r.isPresent());
  }

  @ViewTest
  @Navigate("test-wizard")
  void ensureNavigatingToTheEndWorks(@Select("aire-wizard") Wizard<?> wizard, @Context TestContext $) {
    assertNotNull(wizard);
    wizard.advance();
    wizard.advance();
    val r = $.selectFirst("section.page-3", Page3.class);
    assertTrue(r.isPresent());
    assertFalse(wizard.canAdvance());
  }

  @ViewTest
  @Navigate("test-wizard")
  void ensureParentIsInjectable(@Select("aire-wizard") Wizard<?> wizard, @Context TestContext $) {
    assertInstanceOf(Page1.class, wizard.getCurrentPage());
    wizard.advance();
    assertInstanceOf(Page2.class, wizard.getCurrentPage());
    val page = (Page2) wizard.getCurrentPage();
    assertEquals(page.getWizard(), wizard);
    assertNotNull(page.getModel());
  }

  @ViewTest
  @Navigate("test-wizard")
  void ensureNavigatingBackwardsAndForwardsWorks(@Select Wizard<?> wizard, @Context TestContext $) {
    assertFalse(wizard.canRetreat());
    wizard.advance();
    assertTrue(wizard.canRetreat());
    assertTrue($.selectFirst("section.page-2", Page2.class).isPresent());
    wizard.retreat();
    assertTrue($.selectFirst("section.page-1", Page1.class).isPresent());
  }
}
