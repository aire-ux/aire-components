package io.sunshower.zephyr.configuration;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import io.sunshower.zephyr.security.CompositeRealmManager;
import io.sunshower.zephyr.security.views.AuthenticationView;
import io.sunshower.zephyr.security.views.InitializationWizard;
import lombok.NonNull;
import org.apache.commons.configuration2.Configuration;

public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

  private final Configuration configuration;
  private final CompositeRealmManager realmManager;

  public ConfigureUIServiceInitListener(
      @NonNull final CompositeRealmManager realmManager, Configuration configuration) {
    this.realmManager = realmManager;
    this.configuration = configuration;
  }

  @Override
  public void serviceInit(ServiceInitEvent event) {
    event
        .getSource()
        .addUIInitListener(
            uiEvent -> {
              final UI ui = uiEvent.getUI();
              ui.addBeforeEnterListener(this::beforeEnter);
            });
  }

  private void beforeEnter(BeforeEnterEvent event) {
    if (!isInitialized()) {
      event.rerouteTo(InitializationWizard.class);
    } else {
      if (!SecurityUtils.isUserLoggedIn()) {
        event.rerouteTo(AuthenticationView.class);
      }
    }
  }

  boolean isInitialized() {
    return configuration.getBoolean("aire.initialized", false);
  }
}
