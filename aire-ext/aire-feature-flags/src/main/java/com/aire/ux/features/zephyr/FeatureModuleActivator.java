package com.aire.ux.features.zephyr;

import com.aire.ux.ExtensionRegistration;
import com.aire.ux.Extensions;
import com.aire.ux.Registration;
import com.aire.ux.RouteDefinition.Mode;
import com.aire.ux.Selection;
import com.aire.ux.UserInterface;
import com.aire.ux.features.FeatureDescriptor;
import com.aire.ux.features.InMemoryFeatureManager;
import com.aire.ux.features.SelectionBasedComponentInclusionVoter;
import com.aire.ux.features.ui.FeatureGrid;
import io.sunshower.zephyr.management.PluginTabView;
import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import io.zephyr.api.ServiceTracker;
import lombok.val;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

@Import(FeatureModuleConfiguration.class)
public class FeatureModuleActivator implements ModuleActivator {

  private ServiceTracker registration;
  private Registration extensionRegistration;
  private ExtensionRegistration gridRegistration;
  private AnnotationConfigApplicationContext applicationContext;

  @Override
  public void start(ModuleContext context) throws Exception {
    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.register(FeatureModuleConfiguration.class);
    context.getReferences(UserInterface.class).forEach(ui -> {
      val bean = ui.getDefinition().get();
      applicationContext.registerBean(UserInterface.class, () -> bean);
      initialize(bean);
    });
    applicationContext.refresh();
  }

  @Override
  public void stop(ModuleContext context) throws Exception {
    if (extensionRegistration != null) {
      extensionRegistration.close();
    }
    if (gridRegistration != null) {
      gridRegistration.close();
    }
    if (context != null) {
      applicationContext.stop();
    }
  }

  private void initialize(UserInterface userInterface) {
    val extension =
        Extensions.create(
            ":feature-view",
            (PluginTabView view) -> {
              view.addTab("Feature Flags", FeatureGrid.class);
            });
    try {
      extensionRegistration = userInterface.register(Selection.path(":module-management"),
          extension);
      gridRegistration = userInterface.register(Mode.Global, FeatureGrid.class);
      val manager = InMemoryFeatureManager.getInstance();
      val voter = new SelectionBasedComponentInclusionVoter(manager);
      userInterface.registerComponentInclusionVoter(voter);

      val feature = new FeatureDescriptor("modules.features.feature-grid", "Module Feature Grid",
          ":module-management:feature-view", "Feature view");
      feature.enable();

      manager.registerFeature(feature);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }
}
