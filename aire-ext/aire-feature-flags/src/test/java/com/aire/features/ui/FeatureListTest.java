package com.aire.features.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.features.FeatureManager;
import com.aire.features.RouteDefinitionFeature;
import com.aire.features.zephyr.FeatureModuleConfiguration;
import com.aire.ux.RouteDefinition;
import com.aire.ux.UserInterface;
import com.aire.ux.features.TestConfiguration;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.NotFoundException;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Overlays;
import io.sunshower.zephyr.ui.controls.Switch;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@AireUITest
@Navigate
@Routes(scanClassPackage = MainView.class)
@ContextConfiguration(classes = {TestConfiguration.class, FeatureModuleConfiguration.class})
class FeatureListTest {

  @ViewTest
  @DirtiesContext
  void ensureFeaturesAreConfigured(
      @Autowired UserInterface ui,
      @Autowired FeatureManager featureManager,
      @Context TestContext $) {
    assertEquals(2, ui.getExtensionRegistry().getExtensions().size());
    featureManager.registerFeature(
        new RouteDefinitionFeature(RouteDefinition.fromAnnotatedClass(FeatureList.class)));
    featureManager.enable(FeatureList.class.getCanonicalName());
    $.navigate(FeatureList.class);
    featureManager.disable(FeatureList.class.getCanonicalName());
    assertThrows(NotFoundException.class, () -> $.navigate(FeatureList.class));
  }

  @ViewTest
  @DirtiesContext
  void ensureFeatureListIsNavigable(
      @Autowired UserInterface ui,
      @Autowired FeatureManager featureManager,
      @Context TestContext $) {
    assertEquals(0, featureManager.getDescriptors().size());
    featureManager.registerFeature(
        new RouteDefinitionFeature(RouteDefinition.fromAnnotatedClass(FeatureList.class)));
    featureManager.enable(FeatureList.class.getCanonicalName());
    $.navigate(FeatureList.class);
    $.flush();
    val list = $.selectFirst(FeatureList.class).get();
    val overlay = Overlays.open(list, AddFeatureFlagOverlay.class);
    assertNotNull(overlay);

    val keyField =
        $.downTo(AddFeatureFlagOverlay.class).selectFirst("[name=key]", TextField.class).get();

    val nameField =
        $.downTo(AddFeatureFlagOverlay.class).selectFirst("[name=name]", TextField.class).get();

    val description =
        $.downTo(AddFeatureFlagOverlay.class)
            .selectFirst("[name=description]", TextArea.class)
            .get();

    val path =
        $.downTo(AddFeatureFlagOverlay.class).selectFirst("vaadin-select", Select.class).get();

    val enabled =
        $.downTo(AddFeatureFlagOverlay.class).selectFirst("aire-switch", Switch.class).get();

    keyField.setValue("test-key");
    nameField.setValue("test-name");
    description.setValue("description");
    path.setValue(FeatureList.class.getCanonicalName());
    enabled.setSelected(true);
    overlay.onSuccess(null);
    assertEquals(2, featureManager.getDescriptors().size());

    val descriptor =
        featureManager.getDescriptors().stream()
            .filter(t -> t.getKey().equals("test-key"))
            .findFirst()
            .get();
    assertTrue(descriptor.isEnabled());
  }
}
