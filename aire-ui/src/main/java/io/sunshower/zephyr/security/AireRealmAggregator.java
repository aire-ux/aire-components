package io.sunshower.zephyr.security;

import io.sunshower.model.api.Realm;
import io.sunshower.realms.RealmManager;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public class AireRealmAggregator implements RealmAuthorizer {

  private final CompositeRealmManager realmManager;

  public AireRealmAggregator(CompositeRealmManager realmManager) {
    this.realmManager = realmManager;
  }

  @Override
  public UserDetails updatePassword(UserDetails user, String newPassword) {
    val rm = realmManager.realmManagerFor(user);
    val udetails =
        rm.findByUsername(user.getUsername())
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "Error: username '%s' was not found".formatted(user.getUsername())));

    if (rm instanceof UserDetailsManager rmanager) {
      rmanager.updateUser(udetails);
      udetails.setPassword(newPassword);
      return rmanager.loadUserByUsername(user.getUsername());
    }
    return loadUserByUsername(user.getUsername());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return realmManager.getRealms().stream()
        .flatMap(realm -> realm.findByUsername(username).stream())
        .findFirst()
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    "No username '%s' found in this system".formatted(username)));
  }

  @Override
  public Realm getRealm() {
    return null;
  }

  @Override
  public void save() {
    realmManager.getRealms().forEach(RealmManager::save);
  }

  @Override
  public UserDetailsManager detailsManagerFor(Realm realm) {
    return null;
  }

  @Override
  public UserDetailsManager detailsManagerFor(String realmName) {
    return new RealmUserDetailsManager(realmManager.get(realmName));
  }

  private RealmManager locateRealmForUser(UserDetails user) {
    return realmManager.realmManagerFor(user);
  }

  public RealmManager realmManagerFor(String username) {
    return realmManager.realmManagerFor(username);
  }
}
