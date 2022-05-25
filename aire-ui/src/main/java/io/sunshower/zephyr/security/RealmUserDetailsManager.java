package io.sunshower.zephyr.security;

import io.sunshower.realms.RealmManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public class RealmUserDetailsManager implements UserDetailsManager {

  private final RealmManager realmManager;

  public RealmUserDetailsManager(RealmManager realmManager) {
    this.realmManager = realmManager;
  }

  @Override
  public void createUser(UserDetails user) {
    if (realmManager instanceof UserDetailsManager manager) {
      manager.createUser(user);
    }
  }

  @Override
  public void updateUser(UserDetails user) {
    if (realmManager instanceof UserDetailsManager manager) {
      manager.updateUser(user);
    }
  }

  @Override
  public void deleteUser(String username) {
    if (realmManager instanceof UserDetailsManager manager) {
      manager.deleteUser(username);
    }
  }

  @Override
  public void changePassword(String oldPassword, String newPassword) {
    if (realmManager instanceof UserDetailsManager manager) {
      manager.changePassword(oldPassword, newPassword);
    }
  }

  @Override
  public boolean userExists(String username) {
    return realmManager.findByUsername(username).isPresent();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (realmManager instanceof UserDetailsManager manager) {
      return manager.loadUserByUsername(username);
    }
    return realmManager
        .findByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("No user identified by '%s'".formatted(username)));
  }
}
