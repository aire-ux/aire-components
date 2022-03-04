package com.aire.ux.actions;

import com.aire.ux.Registration;
import com.aire.ux.actions.ActionEvent.Type;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.lang.events.EventListener;
import io.sunshower.lang.events.EventType;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.val;

public abstract class AbstractAction extends AbstractEventSource implements Action {

  private final Key key;
  private final List<Registration> registrations;
  private boolean enabled;

  protected AbstractAction(final @NonNull Key key) {
    this(key, false);
  }

  protected AbstractAction(final @NonNull Key key, boolean enabled) {
    this.key = key;
    setEnabled(enabled);
    registrations = new ArrayList<>();
  }

  @Override
  public Key getKey() {
    return key;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    val type = enabled ? Type.ActionEnabled : Type.ActionDisabled;
    dispatchEvent(type, new DefaultActionEvent(key, this, type));
  }

  public void perform() {}

  public Registration addActionEventListener(
      EventType type, EventListener<ActionEvent<?>> listener) {
    addEventListener(listener, type);
    return () -> removeEventListener(listener);
  }

  public void dispose() {
    for (val reg : registrations) {
      reg.close();
    }
  }
}
