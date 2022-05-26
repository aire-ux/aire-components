package io.sunshower.zephyr.core.modules;

import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventType;
import io.zephyr.api.ModuleEvents;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.Kernel;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ModuleEventDispatcher
    implements DisposableBean,
        ApplicationEventPublisherAware,
        io.sunshower.lang.events.EventListener<Module> {

  private final Kernel kernel;
  private ApplicationEventPublisher publisher;

  @Inject
  public ModuleEventDispatcher(@NonNull Kernel kernel) {
    this.kernel = kernel;
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.publisher = applicationEventPublisher;
  }

  @Override
  public void destroy() {
    kernel.removeEventListener(this);
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void onApplicationReady(ApplicationReadyEvent event) {
    kernel.addEventListener(this, ModuleEvents.STARTED, ModuleEvents.STOPPED);
  }

  @Override
  public void onEvent(EventType type, Event<Module> event) {
    val module = event.getTarget();
    val newState = module.getLifecycle().getState();
    if (type == ModuleEvents.STARTED) {
      publisher.publishEvent(new ModuleStartedEvent(module));
    } else {
      publisher.publishEvent(new ModuleStoppedEvent(module));
    }
  }
}
