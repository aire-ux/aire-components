package com.aire.ux.actions;

import com.aire.ux.actions.ActionEvent.Type;
import io.sunshower.lang.events.EventSource;
import lombok.NonNull;
import lombok.val;

public interface ActionManager extends EventSource {

  boolean enable(Key key);
  boolean disable(Key key);

  boolean enableAll(Key...path);
  boolean disableAll(Key...path);

  default boolean enable(@NonNull Action action) {
    return enable(action.getKey());
  }

  @NonNull
  ActionMap getActionMap();

  default Action locate(Key key) {
    return getActionMap().get(key);
  }

  default void register(@NonNull Action action) {
    getActionMap().add(action);
    dispatchEvent(Type.ActionRegistered,
        new DefaultActionEvent(action, Type.ActionRegistered));
  }

  default Action unregister(@NonNull Action action) {
    val result = unregister(action.getKey());
    dispatchEvent(Type.ActionRegistered,
        new DefaultActionEvent(action, Type.ActionRegistered));
    return result;
  }

  default Action unregister(Key key) {
    return getActionMap().remove(key);
  }
}
