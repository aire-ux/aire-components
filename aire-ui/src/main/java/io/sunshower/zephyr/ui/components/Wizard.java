package io.sunshower.zephyr.ui.components;

import static java.lang.String.format;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.di.Instantiator;
import io.sunshower.arcus.reflect.Reflect;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.val;

@Tag("aire-wizard")
@JsModule("./aire/ui/components/wizard.ts")
@CssImport("./styles/aire/ui/components/wizard.css")
public class Wizard<K> extends HtmlContainer {

  static final TransitionListener<?> NO_OP = new TransitionListener<>() {};
  /** immutable state */
  private final Map<K, WizardStep<K, ?>> steps;

  private final Deque<WizardStep<K, ?>> history;
  private final Map<K, Transition<K>> transitions;
  /** mutable state */
  private WizardStep<K, ?> currentStep;

  public Wizard() {
    steps = new HashMap<>();
    history = new ArrayDeque<>();
    transitions = new HashMap<>();
  }

  public static <K> WizardKeyStepBuilder<K> key(K key) {
    return new WizardKeyStepBuilder(key);
  }

  public <T extends Component> void addStep(Step<K, T> step) {
    steps.put(
        step.getKey(),
        new WizardStep<>(step.getKey(), step.getTitle(), step.getPage(), step.getIconFactory()));
  }

  public <T extends Component> void addStep(@NonNull Class<T> page) {
    val annotatedStep = annotatedStep(page);
    steps.put(annotatedStep.key, annotatedStep);
  }

  @SuppressWarnings("unchecked")
  private <T extends Component> WizardStep<K, ?> annotatedStep(Class<T> page) {

    val pageDefinition = page.getAnnotation(WizardPage.class);
    if (pageDefinition == null) {
      throw new IllegalArgumentException(
          "Error: page type '%s' must be annotated with @WizardPage");
    }
    return new WizardStep<>(
        (K) pageDefinition.key(), pageDefinition.title(), page, pageDefinition.iconFactory());
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

  /**
   * add a transition from <code>from</code> to <code>to</code>
   *
   * @param from the starting state
   * @param to the end state
   * @param listener the listener to apply when the transition is triggered
   */
  public void addTransition(K from, K to, TransitionListener<K> listener) {
    transitions.put(from, new Transition<>(to, listener));
  }

  /**
   * add a transition from <code>from</code> to <code>to</code>
   *
   * @param from the starting state
   * @param to the end state
   */
  @SuppressWarnings("unchecked")
  public void addTransition(K from, K to) {
    addTransition(from, to, (TransitionListener<K>) NO_OP);
  }

  @SuppressWarnings("unchecked")
  public <T extends Component, U extends Component> void addTransition(Class<T> from, Class<U> to) {
    addTransition(from, to, (TransitionListener<K>) NO_OP);
  }

  public <T extends Component, U extends Component> void addTransition(
      Class<T> from, Class<U> to, TransitionListener<K> listener) {
    var fst = lookupStepByType(from);
    if (fst == null) {
      addStep(from);
    }
    fst = lookupStepByType(from);
    assert fst != null;

    var snd = lookupStepByType(to);
    if (snd == null) {
      addStep(to);
    }
    snd = lookupStepByType(to);
    assert snd != null;
    addTransition(fst.key, snd.key, listener);
  }

  /**
   * transition to the next state
   *
   * @return the next state after transition
   * @throws IllegalStateException if the current step is not defined or if there is no transition
   *     from the current step to a next step
   */
  public K advance() {
    if (currentStep == null) {
      throw new IllegalStateException(
          "Error: initial value was not set.  Please call 'setInitial' before trying to advance");
    }

    val next = transitions.get(currentStep.key);
    if (next == null) {
      throw new IllegalStateException(
          format(
              "Error: there is no transition from '%s' to anything.  Please register the transition via 'addTransition'",
              currentStep.key));
    }
    next.listener.beforeTransition(currentStep.key, this, currentStep.component);

    val key = next.key;
    setCurrentStep(steps.get(key));
    return key;
  }

  public K getCurrentStep() {
    if (currentStep == null) {
      throw new IllegalStateException(
          "Current step has not been set.  Have you called 'setInitialStep'?");
    }
    return currentStep.key;
  }

  private void setCurrentStep(WizardStep<K, ?> step) {
    history.push(currentStep);
    currentStep = step;
  }

  public void addSteps(Class<? extends Component>... components) {
    for (val step : components) {
      addStep(step);
    }
  }

  @SuppressWarnings("unchecked")
  public void addSteps(Step<K, ?>... components) {
    for (val step : components) {
      addStep(step);
    }
  }

  protected Instantiator getInstantiator() {
    return Instantiator.get(UI.getCurrent());
  }

  public <T extends Component> void setInitialStep(Class<T> step) {
    var wizardStep = lookupStepByType(step);
    if (wizardStep == null) {
      addStep(step);
    }
    wizardStep = lookupStepByType(step);
    assert wizardStep != null;
    setInitialStep(wizardStep.key);
  }

  @SuppressWarnings("unchecked")
  private <T extends Component> WizardStep<K, T> lookupStepByType(Class<T> step) {
    for (val s : steps.values()) {
      if (s.page.equals(step)) {
        return (WizardStep<K, T>) s;
      }
    }
    return null;
  }

  public interface Step<K, V extends Component> {

    /** @return the key for this step */
    K getKey();

    /** @return the title for this step */
    String getTitle();

    /** @return the icon factory for this step */
    Supplier<Icon> getIconFactory();

    /** @return the component for this wizard */
    Class<V> getPage();
  }

  public interface TransitionListener<K> {

    /**
     * determine if the wizard can transition between this state and the next state
     *
     * @param state the current state
     * @param host the current wizard
     * @param currentPage the current wizard page
     * @return true if the transition can be made
     */
    default boolean canTransition(K state, Wizard<K> host, Component currentPage) {
      return true;
    }

    /**
     * fired before the wizard makes the transition
     *
     * @param state the current state
     * @param host the current wizard
     * @param currentPage the current page
     */
    default void beforeTransition(K state, Wizard<K> host, Component currentPage) {}

    /**
     * fired after the wizard makes the transition
     *
     * @param state the current state
     * @param host the current wizard
     * @param currentPage the current page
     */
    default void afterTransition(K state, Wizard<K> host, Component currentPage) {}
  }

  private static final class WizardStep<K, V extends Component> {

    private final K key;
    private final Class<V> page;
    private final String title;
    private final Supplier<Icon> iconFactory;

    private V component;

    WizardStep(
        @NonNull final K key,
        @NonNull String title,
        @NonNull final Class<V> page,
        Class<? extends Supplier<Icon>> iconFactory) {
      this.key = key;
      this.page = page;
      this.title = title;
      if (!IconSupplier.class.equals(iconFactory)) {
        this.iconFactory = Reflect.instantiate(iconFactory);
      } else {
        this.iconFactory = VaadinIcon.LIST::create;
      }
    }

    public WizardStep(K key, String title, Class<V> component, Supplier<Icon> iconFactory) {
      this.key = key;
      this.title = title;
      this.page = component;
      this.iconFactory = iconFactory;
    }
  }

  private static final class Transition<K> {

    private final K key;
    private final TransitionListener<K> listener;

    Transition(@NonNull final K key, @NonNull TransitionListener<K> listener) {
      this.key = key;
      this.listener = listener;
    }
  }

  public static class WizardKeyStepBuilder<K> {

    private final K key;

    public WizardKeyStepBuilder(@NonNull K key) {
      this.key = key;
    }

    public WizardKeyTitleBuilder<K> title(String title) {
      return new WizardKeyTitleBuilder<K>(key, title);
    }
  }

  public static class WizardKeyTitleBuilder<K> {

    private final K key;
    private final String title;

    public WizardKeyTitleBuilder(@NonNull K key, @NonNull String title) {
      this.key = key;
      this.title = title;
    }

    public <T extends Component> WizardKeyPageBuilder<K, T> page(Class<T> page) {
      return new WizardKeyPageBuilder<>(key, title, page);
    }
  }

  public static class WizardKeyPageBuilder<K, V extends Component> {

    private final K key;
    private final String title;
    private final Class<V> pageType;

    public WizardKeyPageBuilder(@NonNull K key, @NonNull String title, @NonNull Class<V> page) {
      this.key = key;
      this.title = title;
      this.pageType = page;
    }

    public Step<K, V> icon(Supplier<Icon> iconSupplier) {
      return new DefaultStep<K, V>(key, title, pageType, iconSupplier);
    }
  }

  private static class DefaultStep<K, V extends Component> implements Step<K, V> {

    private final K key;
    private final String title;
    private final Class<V> pageType;
    private final Supplier<Icon> iconSupplier;

    public DefaultStep(K key, String title, Class<V> pageType, Supplier<Icon> iconSupplier) {
      this.key = key;
      this.title = title;
      this.pageType = pageType;
      this.iconSupplier = iconSupplier;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public String getTitle() {
      return title;
    }

    @Override
    public Supplier<Icon> getIconFactory() {
      return iconSupplier;
    }

    @Override
    public Class<V> getPage() {
      return pageType;
    }
  }
}
