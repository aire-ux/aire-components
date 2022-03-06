package com.aire.ux.features;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.ExtensionRegistration;
import com.aire.ux.Extensions;
import com.aire.ux.Registration;
import com.aire.ux.RouteDefinition;
import com.aire.ux.Selection;
import com.aire.ux.UserInterface;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.features.scenarios.scenario1.TestFeatureView;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.button.Button;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.management.PluginTabView;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@AireUITest
@Navigate
@Routes(scanClassPackage = MainView.class)
class InMemoryFeatureManagerTest {

  private FeatureManager featureManager;
  private Registration extensionRegistration;
  private ExtensionRegistration routeRegistration;
  private SelectionBasedComponentInclusionVoter featureVoter;

  @BeforeEach
  void setUp(@Autowired ExtensionRegistry registry, @Autowired UserInterface userInterface) {
    featureManager = new InMemoryFeatureManager();
    featureVoter = new SelectionBasedComponentInclusionVoter(featureManager);
    userInterface.registerComponentInclusionVoter(featureVoter);
    routeRegistration = registry.register(RouteDefinition.global(TestFeatureView.class));
    val extension =
        Extensions.create(
            ":test-view",
            (PluginTabView view) -> {
              view.addTab("Sup", TestFeatureView.class);
            });
    extensionRegistration =
        userInterface.register(Selection.path(":application:modules:module-management"), extension);
  }

  @AfterEach
  void tearDown() {
    routeRegistration.close();
    extensionRegistration.close();
  }

  @ViewTest
  void ensureUiIsInjectable(@Context TestContext $) {
    $.navigate(TestFeatureView.class);
    $.flush();
    val button = $.selectFirst("vaadin-button[text*=test]", Button.class);
    assertTrue(button.isPresent());
  }

  @ViewTest
  void ensureDisablingViewPreventsItFromBeingRegistered(@Context TestContext $) {
    val descriptor =
        new FeatureDescriptor(
            "test.view.feature",
            "Test View Feature",
            ":application:modules:module-management:test-view",
            "Cool feature descriptor");
    featureManager.registerFeature(descriptor);
    assertTrue(featureManager.enable("test.view.feature"));

    $.navigate(TestFeatureView.class);
    $.flush();
    var button = $.selectFirst("vaadin-button[text*=test]", Button.class);
    assertTrue(button.isPresent());
    featureManager.disable("test.view.feature");
    $.navigate(MainView.class);
    $.navigate(TestFeatureView.class);
    button = $.selectFirst("vaadin-button[text*=test]", Button.class);
    assertFalse(button.isPresent());
  }
}
