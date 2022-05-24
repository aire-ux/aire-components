package io.sunshower.zephyr.security;

import io.sunshower.crypt.vault.AuthenticationFailedException;
import lombok.val;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CryptkeeperAuthenticationProvider implements AuthenticationProvider {

  final AireRealmAggregator realmAggregator;

  public CryptkeeperAuthenticationProvider(
      AireRealmAggregator realmAggregator) {
    this.realmAggregator = realmAggregator;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    val username = (String) authentication.getPrincipal();
    val realm = realmAggregator.realmManagerFor(username);
    return new UserAuthentication(
        realm.authenticate(username, (String) authentication.getCredentials()).orElseThrow(
            () -> new AuthenticationFailedException(
                "Username/password combination not found in any realm")));


  }

  @Override
  public boolean supports(Class<?> authentication) {
    return true;
  }
}
