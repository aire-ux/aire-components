package io.sunshower.zephyr.security.views;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import io.sunshower.zephyr.MainView;
import javax.inject.Inject;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("login")
@PageTitle("Login")
@CssImport(value = "./styles/themes/login-overlay.css", themeFor = "vaadin-login-overlay-wrapper")
public class AuthenticationView extends VerticalLayout implements BeforeEnterObserver {

  private LoginOverlay overlay;

  @Inject
  public AuthenticationView(AuthenticationManager authenticationManager) {
    addClassName("login-view");
    setSizeFull();
    setAlignItems(Alignment.CENTER);
    overlay = new LoginOverlay();
    overlay.setTitle("Welcome to Zephyr");
    overlay.setDescription("Beautifully Simple Cloud Management");
    overlay.addLoginListener(event -> {
      try {
        val auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(event.getUsername(), event.getPassword()));
        val sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        VaadinSession.getCurrent().setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
//        UI.getCurrent().navigate(MainView.class);
        val ui = UI.getCurrent();
        overlay.close();
        ui.access(() -> {
          ui.navigate(MainView.class);
          ui.push();
        });
      } catch (AuthenticationException ex) {
        overlay.setError(true);
      }
    });

  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if (event.getLocation()
        .getQueryParameters()
        .getParameters()
        .containsKey("error")) {
      overlay.setError(true);
    }
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    overlay.setOpened(true);
    add(overlay);
  }
}
