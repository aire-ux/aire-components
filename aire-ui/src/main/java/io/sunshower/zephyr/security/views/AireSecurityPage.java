package io.sunshower.zephyr.security.views;

import io.sunshower.crypt.core.SecretService;
import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.ui.components.AbstractWizardPage;
import io.sunshower.zephyr.ui.components.FormPanel;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.WizardModelSupport;
import io.sunshower.zephyr.ui.components.WizardPage;
import javax.inject.Inject;

@WizardPage(key = "security-info", title = "Seed Security Information")
public class AireSecurityPage extends AbstractWizardPage<String, SecurityInitializationModel>
    implements WizardModelSupport<String, SecurityInitializationModel> {

  private final FormPanel contents;
  private final SecretService secretService;
  private SecurityInitializationModel model;

  @Inject
  public AireSecurityPage(final SecretService secretService) {
    this.secretService = secretService;
    this.contents = new FormPanel();
    setUpContents();
  }

  private void setUpContents() {
    //    val left = contents.getLeft();
    //    left.setHeader("Seed Security Information");

    //    left.setDescription();
  }

  public void onEntered(Wizard<String, SecurityInitializationModel> host) {}
}
