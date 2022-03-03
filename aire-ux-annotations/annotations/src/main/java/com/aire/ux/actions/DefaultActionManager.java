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

public class DefaultActionManager extends AbstractEventSource implements ActionManager {

  final ActionMap actionMap;
  final List<Registration> registrations;

  public DefaultActionManager() {
    actionMap = new DefaultActionMap();
    registrations = new ArrayList<>();
  }

  @Override
  public Action get(Key key) {
    return actionMap.get(key);
  }

  @Override
  public List<Action> getAll(Key... path) {
    return actionMap.getKeysIn(path);
  }

  @Override
  public boolean enable(Key key) {
    val action = get(key);
    if (action != null) {
      action.enable();
      return true;
    }
    return false;
  }

  @Override
  public boolean disable(Key key) {
    val action = get(key);
    if (action != null) {
      action.disable();
      return true;
    }
    return false;
  }

  @Override
  public boolean enableAll(Key... path) {
    val results = actionMap.getKeysIn(path);
    for (val result : results) {
      result.enable();
    }
    dispatchEvent(Type.BulkActionsEnabled, new BulkActionEvent(Key.BULK, results,
        Type.BulkActionsEnabled));
    return true;
  }

  @Override
  public boolean disableAll(Key... path) {
    val results = actionMap.getKeysIn(path);
    for (val result : results) {
      result.disable();
    }
    dispatchEvent(Type.BulkActionsDisabled, new BulkActionEvent(Key.BULK, results,
        Type.BulkActionsDisabled));
    return true;
  }

  @Override
  public <T> void addEventListener(EventListener<T> listener, EventType... types) {
    super.addEventListener(listener, types);
    registrations.add(() -> removeEventListener(listener));
  }

  @Override
  public <T> void addEventListener(EventListener<T> listener, int options, EventType... types) {
    super.addEventListener(listener, options, types);
    registrations.add(() -> removeEventListener(listener));
  }

  @Override
  public @NonNull ActionMap getActionMap() {
    return actionMap;
  }

  @Override
  public void close() throws Exception {
    for (val registration : registrations) {
      registration.close();
    }
  }
}
