package io.sunshower.zephyr.configuration;

import io.sunshower.crypt.core.SecretService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class VaultDestroyingLogoutHandler implements LogoutHandler {

  private final SecretService service;
  private final ApplicationContext context;

  public VaultDestroyingLogoutHandler(SecretService service, ApplicationContext context) {
    this.service = service;
    this.context = context;
  }

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {}
}
