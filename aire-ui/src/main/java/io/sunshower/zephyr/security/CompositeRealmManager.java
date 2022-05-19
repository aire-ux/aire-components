package io.sunshower.zephyr.security;

import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventListener;
import io.sunshower.lang.events.EventType;
import io.sunshower.realms.RealmManager;
import io.zephyr.api.ServiceEvents;
import io.zephyr.api.ServiceRegistration;
import io.zephyr.kernel.core.Kernel;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.WeakHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class CompositeRealmManager implements EventListener<ServiceRegistration<RealmManager>> {

  private final WeakHashMap<String, RealmManager> realms;

  public CompositeRealmManager(Kernel kernel) {
    realms = new WeakHashMap<>();
    kernel.addEventListener(this, ServiceEvents.REGISTERED, ServiceEvents.UNREGISTERED);
  }



  public void register(RealmManager realmManager) {
    realms.put(realmManager.getName(), realmManager);
  }

  public RealmManager get(String name) {
    val result = realms.get(name);
    if(result == null) {
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

}
