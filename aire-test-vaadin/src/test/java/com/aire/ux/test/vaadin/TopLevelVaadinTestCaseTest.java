package com.aire.ux.test.vaadin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.vaadin.scenarios.classroutes.ClassScenarioRouteLayout;
import com.aire.ux.test.vaadin.scenarios.routes.MainLayout;
import lombok.val;
import org.junit.jupiter.api.Test;

@AireTest
@Navigate("main")
@Routes(scanPackage = "com.aire.ux.test.vaadin.scenarios.routes")
public class TopLevelVaadinTestCaseTest {

  @ViewTest
  @Navigate("main")
  void ensureRoutesAreNavigable() {}

  @ViewTest
  @Navigate("main")
  void ensureTopLevelClassFrameIsActive(@Select MainLayout layout) {
    assertNotNull(layout);
  }

  @Test
  void ensureTestMethodForNormalTestDoesNotGenerateFrameEntry() {
    val method = Frames.resolveCurrentFrame().getContext().getTestMethod();
    assertFalse(method.isPresent());
  }

  @ViewTest
  @Navigate("test")
  @Routes(scanPackage = "com.aire.ux.test.vaadin.scenarios.classroutes")
  void ensureTestMethodWithViewTestGeneratesFrameEntryWithMethod() {
    val method = Frames.resolveCurrentFrame().getContext().getTestMethod();
    assertTrue(method.isPresent());
  }

  @ViewTest
  @Navigate("test")
  @Routes(scanPackage = "com.aire.ux.test.vaadin.scenarios.classroutes")
  void ensureTestMethodGeneratesFrameWithCurrentMethod() throws NoSuchMethodException {
    val method = Frames.resolveCurrentFrame().getContext().getTestMethod();
    assertTrue(method.isPresent());
    val m = method.get();
    assertEquals(
        m,
        TopLevelVaadinTestCaseTest.class.getDeclaredMethod(
            "ensureTestMethodGeneratesFrameWithCurrentMethod"));
  }

  @ViewTest
  @Navigate("test")
  @Routes(scanPackage = "com.aire.ux.test.vaadin.scenarios.classroutes")
  void ensureTestMethodGeneratesFrameWithCurrentMethod2(@Select ClassScenarioRouteLayout layout)
      throws NoSuchMethodException {
    val method = Frames.resolveCurrentFrame().getContext().getTestMethod();
    assertTrue(method.isPresent());
    val m = method.get();
    assertEquals(
        m,
        TopLevelVaadinTestCaseTest.class.getDeclaredMethod(
            "ensureTestMethodGeneratesFrameWithCurrentMethod2", ClassScenarioRouteLayout.class));
  }
}
