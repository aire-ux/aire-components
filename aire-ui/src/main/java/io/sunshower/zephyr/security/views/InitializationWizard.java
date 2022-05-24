package io.sunshower.zephyr.security.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.sunshower.crypt.core.SecretService;
import io.sunshower.lang.common.encodings.Encodings;
import io.sunshower.lang.common.encodings.Encodings.Type;
import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.ui.components.Wizard;
import javax.inject.Inject;
import lombok.val;
import org.apache.commons.configuration2.Configuration;

@Route("aire/initialize")
public class InitializationWizard extends VerticalLayout {

  private final SecretService service;
  private final Configuration configuration;
  private final SecurityInitializationModel model;
  private final Wizard<String, SecurityInitializationModel> wizard;

  @Inject
  public InitializationWizard(final Configuration configuration, final SecretService service) {
    this.service = service;
    this.configuration = configuration;
    this.wizard = createWizard();
    this.model = wizard.getModel();
    add(wizard);
    setSizeFull();
  }

  private Wizard<String, SecurityInitializationModel> createWizard() {
    val wizard =
        new Wizard<String, SecurityInitializationModel>(
            new SecurityInitializationModel(service, Encodings.create(Type.Base58)));
    wizard.addSteps(AireSecurityPage.class, UserInfoPage.class);
    wizard.addTransition(AireSecurityPage.class, UserInfoPage.class);
    wizard.setInitialStep(AireSecurityPage.class);
    return wizard;
  }
}
