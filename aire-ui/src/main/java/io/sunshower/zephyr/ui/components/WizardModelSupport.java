package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;

public interface WizardModelSupport<K, T> {

  boolean addModelElement(T value);

  <V extends Component> boolean transitionTo(Class<V> type);

  boolean transitionTo(K key);

  default void onEntered(Wizard<K> wizard) {}

  default void onExited(Wizard<K> wizard) {}
}
