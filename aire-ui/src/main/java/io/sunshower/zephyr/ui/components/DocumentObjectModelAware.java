package io.sunshower.zephyr.ui.components;

import com.aire.ux.core.decorators.DomAwareComponentDecorator;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.AttachNotifier;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.DetachNotifier;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import lombok.val;
import org.springframework.aop.support.AopUtils;

public interface DocumentObjectModelAware {

  DomAwareComponentDecorator decorator =
      new DomAwareComponentDecorator() {

        @Override
        @SuppressWarnings("unchecked")
        protected <T> Class<T> getTargetClass(Object o) {
          return (Class<T>) AopUtils.getTargetClass(o);
        }
      };

  default Registration register(Object o) {
    val registrations = new ArrayList<Registration>(2);
    if (o instanceof AttachNotifier) {
      val notifier = (AttachNotifier) o;
      registrations.add(
          notifier.addAttachListener(new HierarchyComponentAttachedListener(decorator)));
    }
    if (o instanceof DetachNotifier) {
      val notifier = (DetachNotifier) o;
      registrations.add(
          notifier.addDetachListener(new HierarchyComponentDetatchedListener(decorator)));
    }
    return () -> {
      for (val reg : registrations) {
        reg.remove();
      }
    };
  }
}

final class HierarchyComponentDetatchedListener implements ComponentEventListener<DetachEvent> {

  private final DomAwareComponentDecorator decorator;

  public HierarchyComponentDetatchedListener(DomAwareComponentDecorator decorator) {
    this.decorator = decorator;
  }

  @Override
  public void onComponentEvent(DetachEvent event) {
    decorator.onComponentExited(event.getSource());
  }
}

final class HierarchyComponentAttachedListener implements ComponentEventListener<AttachEvent> {

  private final DomAwareComponentDecorator decorator;

  public HierarchyComponentAttachedListener(DomAwareComponentDecorator decorator) {
    this.decorator = decorator;
  }

  @Override
  public void onComponentEvent(AttachEvent event) {
    decorator.onComponentEntered(event.getSource());
  }
}
