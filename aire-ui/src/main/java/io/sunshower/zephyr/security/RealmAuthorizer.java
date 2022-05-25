package io.sunshower.zephyr.security;

import io.sunshower.model.api.Realm;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;

public interface RealmAuthorizer extends UserDetailsService, UserDetailsPasswordService {

  Realm getRealm();

  void save();

  UserDetailsManager detailsManagerFor(Realm realm);

  UserDetailsManager detailsManagerFor(String realmName);
}
