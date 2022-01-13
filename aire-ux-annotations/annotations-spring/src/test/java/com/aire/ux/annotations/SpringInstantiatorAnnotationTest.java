package com.aire.ux.annotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.annotations.scenario1.FrontPage;
import com.aire.ux.annotations.scenario1.Scenario1;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = Scenario1.class)
@Routes(scanPackage = "com.aire.ux.annotations.scenario1")
public class SpringInstantiatorAnnotationTest {

  @ViewTest
  @Navigate("front-page")
  void ensureFrontPageIsSelectable2(@Select FrontPage page) {
    assertNotNull(page);
  }

  @ViewTest
  @Navigate("front-page")
  void ensureFrontPageIsSelectable(@Select FrontPage page) {
    assertNotNull(page);
  }

  @ViewTest
  @Navigate("front-page")
  void ensureFrontPageIsSelectableById(@Select("#my-id") FrontPage page) {
    assertNotNull(page);
    assertNotNull(page.bean);
  }
}
