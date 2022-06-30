package io.sunshower.zephyr.security.views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.aire.ux.test.Context;
import com.aire.ux.test.Forms;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.RouteLocation;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.View;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import io.sunshower.crypt.core.Leases;
import io.sunshower.crypt.core.SecretService;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.ui.components.Wizard;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

@AireUITest
@RouteLocation(scanClassPackage = MainView.class)
@RouteLocation(scanClassPackage = AuthenticationView.class)
@RouteLocation(scanClassPackage = InitializationWizard.class)
class InitializationWizardTest {

  @ViewTest
  @Navigate("aire/initialize")
  void ensurePageIsInjectable(@View AireSecurityPage page) {
    assertNotNull(page);
  }

  @ViewTest
  void ensurePageIsConfiguredCorrectly(@Context TestContext $, @View AireSecurityPage start,
      @View InitializationWizard wizardContainer,
      @View Wizard<String, SecurityInitializationModel> model) {

    assertNotNull($);
    assertNotNull(start);
    assertNotNull(wizardContainer);
    assertNotNull(model);
  }

  @ViewTest
  void ensureRetrievingInitialValueWorks(@Context TestContext $) {
    $.navigate("aire/initialize");
    val initializationWizard = $.selectFirst(InitializationWizard.class).get();
    val wizard = initializationWizard.getWizard();
    val model = wizard.getModel();
    val value = $.downTo(FormLayout.class).selectFirst(TextField.class).get().getValue();
    assertEquals(model.getInitialParameters(), value);
  }

  @ViewTest
  @DirtiesContext(hierarchyMode = HierarchyMode.EXHAUSTIVE, methodMode = MethodMode.AFTER_METHOD)
  void ensureAuthenticationDoesNotWorkInitially(
      @Autowired AuthenticationManager authenticationManager) {
    assertThrows(NoSuchElementException.class, () -> {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken("josiah@sunshower.io", "password"));
    });
  }

  @ViewTest
  void ensureNavigatingEntireWizardWorks(@Context TestContext $, @Autowired Configuration cfg,
      @Autowired
          SecretService service, @Autowired AuthenticationManager authenticationManager) {
    $.navigate("aire/initialize");
    val initializationWizard = $.selectFirst(InitializationWizard.class).get();
    val wizard = initializationWizard.getWizard();
    wizard.advance();
    Forms.populate($)
        .field("vaadin-form-layout > vaadin-text-field:nth-child(1)").value("josiah")
        .field("vaadin-form-layout > vaadin-text-field:nth-child(2)").value("haswell")
        .field("vaadin-form-layout > vaadin-email-field:nth-child(3)").value("josiah@sunshower.io")
        .field("vaadin-form-layout > vaadin-password-field:nth-child(4)").value("password")
        .field("vaadin-form-layout > vaadin-password-field:nth-child(5)").value("password");

    $.selectFirst("vaadin-button[key='next']", Button.class).get().click();
    val vaultId = cfg.getString("primary.vault.id");
    assertNotNull(vaultId);

    val vault = service.lease(Identifier.valueOf(vaultId),
        Leases.forPassword("password").expiresIn(1, TimeUnit.SECONDS));
    assertNotNull(vault);
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken("josiah@sunshower.io", "password"));


  }

}