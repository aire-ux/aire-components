package io.sunshower.zephyr.ui.components;

import static java.lang.String.format;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.val;

@Tag("aire-wizard")
@JsModule("./aire/ui/components/wizard.ts")
@CssImport("./styles/aire/ui/components/wizard.css")
public class Wizard<K> extends HtmlContainer {


  /**
   * immutable state
   */
  private final Map<K, K> transitions;
  private final Map<K, Step<K, ?>> steps;
  private final Deque<Step<K, ?>> history;

  /**
   * mutable state
   */
  private Step<K, ?> currentStep;


  public Wizard() {
    steps = new HashMap<>();
    history = new ArrayDeque<>();
    transitions = new HashMap<>();
  }

  public <T extends Component> void addStep(@NonNull K key, @NonNull Class<T> page) {
    steps.put(key, new Step<>(key, page));
  }

  public void setInitialStep(@NonNull K key) {
    val step = steps.get(key);
    if (step == null) {
      throw new IllegalArgumentException(
          format(
              "Error: state-key '%s' is not registered--please register it via 'addStep' before calling this method",
              key));
    }
    currentStep = step;
  }

  public void addTransition(K from, K to) {
    transitions.put(from, to);
  }

  public K advance() {
    if (currentStep == null) {
      throw new IllegalStateException(
          "Error: initial value was not set.  Please call 'setInitial' before trying to advance");
    }

    val next = transitions.get(currentStep.key);
    if (next == null) {
      throw new IllegalStateException(format(
          "Error: there is no transition from '%s' to anything.  Please register the transition via 'addTransition'",
          currentStep.key));
    }

    setCurrentStep(steps.get(next));
    return next;
  }


  private void setCurrentStep(Step<K, ?> step) {
    history.push(currentStep);
    currentStep = step;
  }

  private static final class Step<K, V> {

    final K key;
    final Class<V> page;

    Step(@NonNull final K key, @NonNull final Class<V> page) {
      this.key = key;
      this.page = page;
    }

  }

}
