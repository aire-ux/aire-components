package io.sunshower.zephyr.security.views;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import io.sunshower.crypt.core.Leases;
import io.sunshower.crypt.core.SecretService;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.security.CompositeRealmManager;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("login")
@PageTitle("Login")
// @CssImport(value = "./styles/themes/login-overlay.css", themeFor =
// "vaadin-login-overlay-wrapper")
public class AuthenticationView extends VerticalLayout implements BeforeEnterObserver {

  private final SecretService secretService;
  private final ApplicationContext context;
  private final Configuration configuration;
  private final CompositeRealmManager realmManager;
  private final AuthenticationManager authenticationManager;

  private LoginOverlay overlay;

  @Inject
  public AuthenticationView(
      Configuration configuration,
      AuthenticationManager authenticationManager,
      CompositeRealmManager realmManager,
      SecretService secretService,
      ApplicationContext context) {
    this.context = context;
    this.configuration = configuration;
    this.realmManager = realmManager;
    this.secretService = secretService;
    this.authenticationManager = authenticationManager;

    addClassName("login-view");
    setSizeFull();
    setAlignItems(Alignment.CENTER);
    overlay = new LoginOverlay();
    overlay.setTitle("Welcome to Zephyr");
    overlay.setDescription("Beautifully Simple Cloud Management");
    overlay.addLoginListener(this::handleLoginAttempt);
  }

  private void handleLoginAttempt(AbstractLogin.LoginEvent event) {
    try {
      if (realmManager.getRealms().isEmpty()) {
        realmManager.bootstrap(event.getPassword());
      }
      val auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(event.getUsername(), event.getPassword()));
      val sc = SecurityContextHolder.getContext();
      sc.setAuthentication(auth);
      VaadinSession.getCurrent().setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
      //        UI.getCurrent().navigate(MainView.class);
      val ui = UI.getCurrent();
      overlay.close();
      ui.access(
          () -> {
            ui.navigate(MainView.class);
            ui.push();
          });
      doRegisterVault(secretService, event.getPassword());
    } catch (Exception ex) {
      overlay.setError(true);
    }
  }

  private void doRegisterVault(SecretService secretService, String password) {
    val vaultId = Identifier.valueOf(configuration.getString("primary.vault.id"));
    val lease =
        secretService.lease(vaultId, Leases.forPassword(password).expiresIn(1, TimeUnit.DAYS));

    val bean =
        BeanDefinitionBuilder.genericBeanDefinition(DelegatingVault.class)
            .addConstructorArgValue(lease.get())
            .setScope("vaadin-session")
            .setDestroyMethodName("close")
            .getBeanDefinition();
    ((BeanDefinitionRegistry) context.getAutowireCapableBeanFactory())
        .registerBeanDefinition("default-vault", bean);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
      overlay.setError(true);
    }
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    overlay.setOpened(true);
    add(overlay);
  }
}
