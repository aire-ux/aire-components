package com.aire.features.zephyr;

import static com.aire.ux.Selection.path;

import com.aire.features.FeatureManager;
import com.aire.features.InMemoryFeatureManager;
import com.aire.features.RouteDefinitionFeature;
import com.aire.features.SelectionBasedComponentInclusionVoter;
import com.aire.features.ui.FeatureList;
import com.aire.ux.Extensions;
import com.aire.ux.Registration;
import com.aire.ux.RouteDefinition;
import com.aire.ux.RouteDefinition.Scope;
import com.aire.ux.UserInterface;
import io.sunshower.zephyr.management.PluginTabView;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeatureModuleConfiguration implements DisposableBean {

  private final List<Registration> registrations;
  @Autowired private ApplicationContext context;
  @Autowired private UserInterface userInterface;

  public FeatureModuleConfiguration() {
    registrations = new ArrayList<>();
  }

  @Bean
  @Lazy
  @Primary
  public FeatureManager featureManager(@Autowired UserInterface userInterface) {
    val manager = new InMemoryFeatureManager(userInterface);
    val extension =
        Extensions.create(
            ":feature-view",
            (PluginTabView view) -> {
              view.addTab("Feature Flags", FeatureList.class);
            });

    manager.registerFeature(
        new RouteDefinitionFeature(RouteDefinition.fromAnnotatedClass(FeatureList.class)));
    registrations.add(userInterface.register(Scope.Global, FeatureList.class));
    registrations.add(userInterface.register(path(":module-management"), extension));
    registrations.add(
        userInterface
            .getComponentInclusionManager()
            .register(new SelectionBasedComponentInclusionVoter(manager)));

    return manager;
  }

  @Override
  public void destroy() throws Exception {
    for (val registration : registrations) {
      registration.close();
    }
  }
}
