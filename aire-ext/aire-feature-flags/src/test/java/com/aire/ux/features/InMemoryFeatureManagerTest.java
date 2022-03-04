package com.aire.ux.features;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.UserInterface;
import com.aire.ux.features.scenarios.scenario1.TestFeatureView;
import com.aire.ux.test.Routes;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.AireUITest;
import org.springframework.beans.factory.annotation.Autowired;

@AireUITest
@Routes(scanClassPackage = TestFeatureView.class)
class InMemoryFeatureManagerTest {

  @ViewTest
  void ensureUiIsInjectable(@Autowired UserInterface userInterface) {
    assertNotNull(userInterface);
  }

}