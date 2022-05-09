package io.sunshower.zephyr.security.views;

import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.ui.components.AbstractWizardPage;
import io.sunshower.zephyr.ui.components.WizardPage;

@WizardPage(key = "user-info-page", title = "Administrator Info")
public class UserInfoPage extends AbstractWizardPage<String, SecurityInitializationModel> {}
