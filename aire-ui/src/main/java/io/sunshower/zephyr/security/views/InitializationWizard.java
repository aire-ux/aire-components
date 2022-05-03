package io.sunshower.zephyr.security.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.components.Wizard;
import javax.inject.Inject;
import lombok.val;
import org.apache.commons.configuration2.Configuration;

@Route("aire/initialize")
public class InitializationWizard extends VerticalLayout {

  private final Wizard<String> wizard;
  private final Configuration configuration;

  @Inject
  public InitializationWizard(final Configuration configuration) {
    this.configuration = configuration;
    this.wizard = createWizard();
    add(wizard);
  }

  private Wizard<String> createWizard() {
    val wizard = new Wizard<String>();
    wizard.addSteps(AireSecurityPage.class, UserInfoPage.class, SavePage.class);
    wizard.setInitialStep(AireSecurityPage.class);
    wizard.addTransition(AireSecurityPage.class, UserInfoPage.class);
    wizard.addTransition(UserInfoPage.class, SavePage.class);
    return wizard;
  }
}
