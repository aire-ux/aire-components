package io.sunshower.zephyr.ui.layout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.AfterNavigationEvent;
import java.util.Optional;
import lombok.val;

public class Layouts {

  @SuppressWarnings("unchecked")
  public static <T extends HasElement> Optional<T> locateFirst(
      AfterNavigationEvent event, Class<T> type) {

    val chain = event.getActiveChain();
    val iter = chain.listIterator();
    while (iter.hasPrevious()) {
      val t = iter.previous();
      if (type.isAssignableFrom(t.getClass())) {
        return (Optional<T>) Optional.of(t);
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public static <T extends HasElement> Optional<T> locateFirst(AttachEvent event, Class<T> type) {
    var source = event.getSource();
    while (source != null && !type.isAssignableFrom(source.getClass())) {
      source = source.getParent().orElse(null);
    }
    return Optional.ofNullable((T) source);
  }

  public static <T extends HasElement> Optional<T> locateFirst(DetachEvent event, Class<T> type) {
    var source = event.getSource();
    while (source != null && !type.isAssignableFrom(source.getClass())) {
      source = source.getParent().orElse(null);
    }
    return Optional.ofNullable((T) source);
  }
}
