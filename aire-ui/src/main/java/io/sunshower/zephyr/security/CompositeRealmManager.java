package io.sunshower.zephyr.security;

import io.sunshower.lang.common.encodings.Encodings;
import io.sunshower.lang.common.encodings.Encodings.Type;
import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventListener;
import io.sunshower.lang.events.EventType;
import io.sunshower.realms.RealmManager;
import io.sunshower.realms.cryptkeeper.FileBackedCryptKeeperRealm;
import io.sunshower.realms.cryptkeeper.RealmConfiguration;
import io.zephyr.api.ServiceEvents;
import io.zephyr.api.ServiceRegistration;
import io.zephyr.kernel.core.Kernel;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.WeakHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@SuppressWarnings("PMD")
public class CompositeRealmManager implements EventListener<ServiceRegistration<RealmManager>> {

  private final Path userDatabaseFile;
  private final Configuration configuration;
  private final WeakHashMap<String, RealmManager> realms;

  public CompositeRealmManager(Kernel kernel, Path userDatabaseFile, Configuration configuration) {
    this.realms = new WeakHashMap<>();
    this.configuration = configuration;
    this.userDatabaseFile = userDatabaseFile;
    kernel.addEventListener(this, ServiceEvents.REGISTERED, ServiceEvents.UNREGISTERED);
  }

  public RealmManager realmManagerFor(String username) {
    for (val realmManager : realms.values()) {
      if (realmManager.findByUsername(username).isPresent()) {
        return realmManager;
      }
    }
    throw new NoSuchElementException("No realm for user: %s".formatted(username));
  }

  public RealmManager realmManagerFor(UserDetails user) {
    return realmManagerFor(user.getUsername());
  }

  public void bootstrap(CharSequence plaintextPassword) {
    val encoding = Encodings.create(Type.Base58);
    val iv = encoding.decode(configuration.getString("default.realm.iv"));
    val salt = encoding.decode(configuration.getString("default.realm.salt"));
    val realm =
        new FileBackedCryptKeeperRealm(
            new RealmConfiguration(userDatabaseFile.toFile(), plaintextPassword, salt, iv));
    register(realm);
  }

  public void register(RealmManager realmManager) {
    realms.put(realmManager.getName(), realmManager);
  }

  public RealmManager get(String name) {
    val result = realms.get(name);
    if (result == null) {
      throw new NoSuchElementException("Error: No realm with name: %s".formatted(name));
    }
    return result;
  }

  @Override
  public void onEvent(EventType type, Event<ServiceRegistration<RealmManager>> event) {
    val realm = event.getTarget().getReference().getDefinition().get();
    if (Objects.equals(type, ServiceEvents.REGISTERED)) {
      realms.put(realm.getName(), realm);
    } else {
      realms.remove(realm.getName());
    }
  }

  public Collection<RealmManager> getRealms() {
    return Collections.unmodifiableCollection(realms.values());
  }

  public void save() {
    for (val realm : getRealms()) {
      realm.save();
    }
  }
}
