package io.sunshower.zephyr.security;

import io.sunshower.model.api.User;
import io.sunshower.model.api.UserDetails;
import java.util.Collection;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

final class UserAuthentication implements Authentication {

  final User user;
  private boolean authenticated;

  public UserAuthentication(@NonNull User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getAuthorities();
  }

  @Override
  public String getCredentials() {
    return user.getPassword();
  }

  @Override
  public UserDetails getDetails() {
    return user.getDetails();
  }

  @Override
  public String getPrincipal() {
    return user.getUsername();
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    this.authenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    return getPrincipal();
  }
}
