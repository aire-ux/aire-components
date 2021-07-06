package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import com.aire.ux.theme.context.ThemeChangeListener.ThemeChangeEvent;
import com.aire.ux.theme.context.ThemeChangeListener.ThemeChangeEventType;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.lang.events.EventListener;
import io.sunshower.lang.events.EventSource;
import io.sunshower.lang.events.EventType;
import java.util.Optional;
import lombok.val;

public class AireThemeManager {

  private static final EventSource eventSource;

  static {
    eventSource = new AireThemeManagerEventSource();
  }

  /** @return the number of event listeners registered */
  public static int getListenerCount() {
    return eventSource.getListenerCount();
  }
  ;

  /**
   * @param types the event-types to check for corresponding listeners for
   * @return true if there exists at least one listener for all the provided event types
   */
  public static boolean listensFor(EventType... types) {
    return eventSource.listensFor(types);
  }

  /**
   * @param listener the listener to register
   * @param types the event types to bind the listener to
   * @param <T> the type-parameter of the event
   * @return a registration that will remove the event listener
   */
  public static <T> Registration addEventListener(EventListener<T> listener, EventType... types) {
    eventSource.addEventListener(listener, types);
    return new Registration() {
      @Override
      public void remove() {
        eventSource.removeEventListener(listener);
      }

      @Override
      public void close() {
        remove();
      }
    };
  }

  /**
   * @param listener the listener to register
   * @param modes the event modes to register the listener under
   * @param eventTypes the event-types
   * @param <T> the event-type type parameter
   * @return a registration that will remove the listener upon closing
   */
  public static <T> Registration addEventListener(
      EventListener<T> listener, int modes, EventType... eventTypes) {
    eventSource.addEventListener(listener, modes, eventTypes);
    return new Registration() {
      @Override
      public void remove() {
        eventSource.removeEventListener(listener);
      }

      @Override
      public void close() {
        remove();
      }
    };
  }

  /**
   * @param var1 the event listener to remove
   * @param <T> the event type parameter
   */
  public static <T> void removeEventListener(EventListener<T> var1) {
    eventSource.removeEventListener(var1);
  }

  public static <T extends Theme> void setTheme(Class<T> themeType) {
    val theme = Reflect.instantiate(themeType);
    ThemeContextHolder.getContext().setTheme(theme);
    eventSource.dispatchEvent(ThemeChangeEventType.ThemeChanged, new ThemeChangeEvent(theme));
  }

  public static Optional<Theme> clearTheme() {
    return ThemeContextHolder.getContext().clearTheme();
  }

  public static Theme getCurrent() {
    return ThemeContextHolder.getContext().getTheme();
  }

  /** convenience interface for removing listeners */
  public interface Registration extends AutoCloseable {

    void remove();
  }

  static class AireThemeManagerEventSource extends AbstractEventSource {}
}
