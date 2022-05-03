package io.sunshower.zephyr.security.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.ui.components.Wizard;
import javax.inject.Inject;
import lombok.val;
import org.apache.commons.configuration2.Configuration;

@Route("aire/initialize")
public class InitializationWizard extends VerticalLayout {

  private final Configuration configuration;
  private final Wizard<String, SecurityInitializationModel> wizard;

  @Inject
  public InitializationWizard(final Configuration configuration) {
    this.configuration = configuration;
    this.wizard = createWizard();
    add(wizard);
  }

  private Wizard<String, SecurityInitializationModel> createWizard() {
    val wizard = new Wizard<String, SecurityInitializationModel>(new SecurityInitializationModel());
    wizard.addSteps(AireSecurityPage.class, UserInfoPage.class, SavePage.class);
    wizard.setInitialStep(AireSecurityPage.class);
    wizard.addTransition(AireSecurityPage.class, UserInfoPage.class);
    wizard.addTransition(UserInfoPage.class, SavePage.class);
    return wizard;
  }
}
