package com.aire.features.zephyr;

import static com.aire.ux.Selection.path;

import com.aire.features.FeatureDescriptor;
import com.aire.features.FeatureManager;
import com.aire.features.InMemoryFeatureManager;
import com.aire.features.ui.FeatureGrid;
import com.aire.ux.Extensions;
import com.aire.ux.Registration;
import com.aire.ux.RouteDefinition.Mode;
import com.aire.ux.UserInterface;
import io.sunshower.zephyr.management.PluginTabView;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureModuleConfiguration implements DisposableBean {

  private final List<Registration> registrations;
  @Autowired
  private ApplicationContext context;
  @Autowired
  private UserInterface userInterface;

  public FeatureModuleConfiguration() {
    registrations = new ArrayList<>();
  }

  @Bean
  public FeatureManager featureManager() {
    val manager = new InMemoryFeatureManager();
    val extension =
        Extensions.create(
            ":feature-view",
            (PluginTabView view) -> {
              view.addTab("Feature Flags", FeatureGrid.class);
            });

    registrations.add(userInterface.register(Mode.Global, FeatureGrid.class));
    registrations.add(userInterface.register(path(":module-management"), extension));

    val feature = new FeatureDescriptor("modules.features.feature-grid", "Module Feature Grid",
        ":module-management:feature-view", "Feature view");
    feature.enable();
    manager.registerFeature(feature);
    return manager;
  }



  @Override
  public void destroy() throws Exception {
    for(val registration : registrations) {
      registration.close();
    }
  }
}
