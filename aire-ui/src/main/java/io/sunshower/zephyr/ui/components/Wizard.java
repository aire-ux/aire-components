package io.sunshower.zephyr.ui.components;

import static java.lang.String.format;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.zephyr.aire.DynamicInstantiator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.val;

/**
 * A wizard is a structured information flow. The wizard has a <i>Key</i> type, which is how
 * individual pages are identified within the flow, and a value-type, which is the type of the
 * model
 *
 * @param <K>
 * @param <V>
 */
@Tag("aire-wizard")
@SuppressWarnings("PMD")
@JsModule("./aire/ui/components/wizard.ts")
@CssImport("./styles/aire/ui/components/wizard.css")
public class Wizard<K, V> extends HtmlContainer {

  public static final String COMPLETE = "complete";
  public static final String NOT_COMPLETE = "not-complete";
  public static final String LEAVING = "leaving";
  public static final String ENTERING = "entering";
  static final TransitionListener<?, ?> NO_OP = new TransitionListener<>() {
  };
  /**
   * immutable state
   */
  private final Nav header;

  private final Map<K, WizardStep<K, ?>> steps;
  private final Deque<WizardStep<K, ?>> history;
  private final Map<K, Transition<K, V>> transitions;
  private V model;
  /**
   * mutable state
   */
  private WizardStep<K, ?> currentStep;

  public Wizard() {
    steps = new HashMap<>();
    history = new ArrayDeque<>();
    transitions = new HashMap<>();
    header = createHeader();
    add(header);
  }

  public Wizard(@NonNull V model) {
    this();
    this.model = model;
  }

  @SuppressWarnings("unchecked")
  public static <K> WizardKeyStepBuilder<K> key(K key) {
    return new WizardKeyStepBuilder<>(key);
  }

  @NonNull
  public V getModel() {
    return model;
  }

  public void setModel(@NonNull V model) {
    this.model = model;
  }

  public <T extends Component> void addStep(Step<K, T> step) {
    steps.put(
        step.getKey(),
        new WizardStep<>(step.getKey(), step.getTitle(), step.getPage(), step.getIconFactory()));
  }

  public boolean canRetreat() {
    return !history.isEmpty();
  }

  public boolean canAdvance() {
    if (currentStep == null) {
      return false;
    }
    val next = transitions.get(currentStep.key);
    if (next != null) {
      return true;
    }
    return false;
  }

  public <T extends Component> void addStep(@NonNull Class<T> page) {
    val annotatedStep = annotatedStep(page);
    steps.put(annotatedStep.key, annotatedStep);
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
   * @param from     the starting state
   * @param to       the end state
   * @param listener the listener to apply when the transition is triggered
   */
  public void addTransition(K from, K to, TransitionListener<K, V> listener) {
    transitions.put(from, new Transition<>(to, listener));
  }

  /**
   * add a transition from <code>from</code> to <code>to</code>
   *
   * @param from the starting state
   * @param to   the end state
   */
  @SuppressWarnings("unchecked")
  public void addTransition(K from, K to) {
    addTransition(from, to, (TransitionListener<K, V>) NO_OP);
  }

  @SuppressWarnings("unchecked")
  public <T extends Component, U extends Component> void addTransition(Class<T> from, Class<U> to) {
    addTransition(from, to, (TransitionListener<K, V>) NO_OP);
  }

  @SuppressWarnings("unchecked")
  public Registration addWizardStateChangedEventListener(
      ComponentEventListener<WizardStateChangedEvent<K, V>> listener) {
    return addListener(
        (Class<WizardStateChangedEvent<K, V>>) (Class) WizardStateChangedEvent.class, listener);
  }

  public <T extends Component> boolean transitionTo(Class<T> type) {
    val step = lookupStepByType(type);
    if (step != null) {
      history.add(currentStep);
      this.updatePage(currentStep, step);
      return true;
    }
    return false;
  }

  public boolean transitionTo(K key) {
    val step = steps.get(key);
    if (step != null) {
      history.add(step);
      this.updatePage(currentStep, step);
      return true;
    }
    return false;
  }

  /**
   * @param from     the annotated source state
   * @param to       the annotated target state
   * @param listener the listener to bind to the transition
   * @param <T>      the type-parameter of the source state
   * @param <U>      the type-parameter of the target state
   */
  public <T extends Component, U extends Component> void addTransition(
      Class<T> from, Class<U> to, TransitionListener<K, V> listener) {
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

  public K retreat() {
    if (!canRetreat()) {
      throw new IllegalStateException("Error: no previous states!");
    }

    val previous = history.pop();
    val currentKey = currentStep.key;
    setCurrentStep(previous);
    this.fireEvent(new WizardStateChangedEvent<>(this, false, currentKey, previous.key));
    return previous.key;
  }

  /**
   * transition to the next state
   *
   * @return the next state after transition
   * @throws IllegalStateException if the current step is not defined or if there is no transition
   *                               from the current step to a next step
   */
  public K advance() {
    checkCurrentStep();

    val next = transitions.get(currentStep.key);
    if (next == null) {
      throw new IllegalStateException(
          format(
              "Error: there is no transition from '%s' to anything.  Please register the transition via 'addTransition'",
              currentStep.key));
    }
    val currentKey = currentStep.key;
    val currentComponent = currentStep.component;
    if (next.listener.canTransition(currentKey, this, currentComponent)) {
      next.listener.beforeTransition(currentKey, this, currentComponent);
      history.push(currentStep);
      val key = next.key;
      setCurrentStep(steps.get(key));
      next.listener.afterTransition(currentKey, this, currentComponent);
      this.fireEvent(new WizardStateChangedEvent<>(this, false, currentKey, next.key));
      return key;
    }
    return currentStep.key;
  }

  @SuppressWarnings("unchecked")
  public <T extends Component> T getCurrentPage() {
    if (currentStep == null) {
      throw new IllegalStateException(
          "Current step has not been set.  Have you called 'setInitialStep'?");
    }
    return (T) currentStep.component;
  }

  public K getCurrentStep() {
    if (currentStep == null) {
      throw new IllegalStateException(
          "Current step has not been set.  Have you called 'setInitialStep'?");
    }
    return currentStep.key;
  }

  private void setCurrentStep(WizardStep<K, ?> step) {
    updatePage(currentStep, step);
  }

  @SafeVarargs
  public final void addSteps(Class<? extends Component>... components) {
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

  public <T extends Component> void setInitialStep(Class<T> step) {
    var wizardStep = lookupStepByType(step);
    if (wizardStep == null) {
      addStep(step);
    }
    wizardStep = lookupStepByType(step);
    assert wizardStep != null;
    setInitialStep(wizardStep.key);
  }

  protected final List<WizardStep<K, ?>> getSteps() {
    if (currentStep == null) {
      throw new IllegalStateException("No initial step");
    }
    var current = currentStep.key;
    val results = new ArrayList<WizardStep<K, ?>>();
    while (current != null) {
      val step = steps.get(current);
      results.add(step);
      val next = transitions.get(step.key);
      if (next != null) {
        current = next.key;
      } else {
        current = null;
      }
    }
    return results;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    validate();
    assert currentStep != null;
    createProgressView(getSteps());
    updatePage(currentStep, currentStep);
  }

  protected void createProgressView(List<WizardStep<K, ?>> steps) {
    val list = new UnorderedList();
    list.getElement().setAttribute("part", "wizard-list");
    for (val step : steps) {
      val listItem = new ListItem();
      if (step.iconFactory != null) {
        listItem.add(step.iconFactory.create());
      }
      val title = new Span(new Text(step.title));
      listItem.add(title);
      list.add(listItem);
    }
    header.add(list);
    add(header);
  }

  protected <T> T instantiate(Class<T> type) {
    return DynamicInstantiator.create(getUI().orElse(UI.getCurrent()), type, this, model)
        .orElseThrow();
  }

  protected Nav createHeader() {
    val nav = new Nav();
    nav.getElement().setAttribute("slot", "header");
    return nav;
  }

  protected void access(Command command) {
    UI.getCurrent().accessSynchronously(command);
  }

  private void checkCurrentStep() {
    if (currentStep == null) {
      throw new IllegalStateException(
          "Error: initial value was not set.  Please call 'setInitial' before trying to advance");
    }
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

  @SuppressWarnings("unchecked")
  private <T extends Component> WizardStep<K, ?> annotatedStep(Class<T> page) {

    val pageDefinition = page.getAnnotation(WizardPage.class);
    if (pageDefinition == null) {
      throw new IllegalArgumentException(
          format("Error: page type '%s' must be annotated with @WizardPage", page));
    }
    return new WizardStep<>(
        (K) pageDefinition.key(), pageDefinition.title(), page, pageDefinition.iconFactory());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void updatePage(WizardStep<K, ?> previous, WizardStep<K, ?> step) {
    access(
        () -> {
          Component pcomponent = null;
          if (!(previous == null || previous.component == null)) {
            pcomponent = previous.component;
            assert pcomponent != null;
            if (pcomponent instanceof TransitionListener) {
              val ptl = (TransitionListener<K, V>) pcomponent;
              if (!ptl.canTransition(previous.key, this, pcomponent)) {
                return;
              } else {
                ptl.beforeTransition(previous.key, this, pcomponent);
              }
            }
            addClass(previous.component, LEAVING);
            removeClass(previous.component, ENTERING);
            removePrevious(previous);
          }
          updatePage(step, getComponent(step));
          if (pcomponent instanceof TransitionListener<?, ?>) {
            ((TransitionListener<K, V>) pcomponent).afterTransition(previous.key, this, pcomponent);
          }
        });
  }

  private Component getComponent(WizardStep<K, ?> step) {
    final Component page;
    if (step.component != null) {
      page = step.component;
    } else {
      page = instantiate(step.page);
    }
    return page;
  }

  @SuppressWarnings("unchecked")
  private void updatePage(WizardStep<K, ?> step, Component page) {
    addClass(page, ENTERING);
    removeClass(page, LEAVING);
    page.getElement().setAttribute("slot", "page");
    ((WizardStep<K, Component>) step).setComponent(page);
    add(page);
    if (page instanceof WizardModelSupport) {
      ((WizardModelSupport) page).onEntered(this);
    }
    currentStep = step;
    updateProgressView(step);
  }

  private void removeClass(@NonNull Component page, @NonNull String type) {
    page.getElement().getClassList().remove(type);
  }

  private void addClass(@NonNull Component page, @NonNull String type) {
    page.getElement().getClassList().add(type);
  }

  private void removePrevious(WizardStep<K, ?> previous) {
    val pcomp = previous.component;
    remove(pcomp);
    pcomp.getElement().removeFromTree();
    if (pcomp instanceof WizardModelSupport) {
      ((WizardModelSupport) pcomp).onExited(this);
    }
  }

  private void updateProgressView(WizardStep<K, ?> step) {
    header
        .getChildren()
        .filter(child -> child.getClass().equals(UnorderedList.class))
        .findFirst().ifPresent(listItem -> {
          val iter = listItem.getChildren().iterator();
          for (int i = 0; i < steps.size(); i++) {
            val cl = iter.next().getElement().getClassList();
            if (i <= history.size()) {
              cl.remove(NOT_COMPLETE);
              cl.add(COMPLETE);
            } else {
              cl.remove(COMPLETE);
              cl.add(NOT_COMPLETE);
            }
          }
        });

  }

  /**
   * ensure that all states are connected
   */
  private void validate() {
    checkCurrentStep();

    var current = currentStep.key;
    val allSteps = new ArrayList<K>();

    while (current != null) {
      if (allSteps.size() > steps.size()) {
        val keys = new HashSet<>(steps.keySet());
        allSteps.removeAll(keys);
        throw new IllegalStateException(
            format("Error: cyclic wizard.  Cycle exists in '%s'", keys));
      }
      allSteps.add(current);
      val next = transitions.get(current);
      if (next != null) {
        current = next.key;
      } else {
        current = null;
      }
    }
    if (allSteps.size() < steps.size()) {
      val keys = new HashSet<>(steps.keySet());
      allSteps.forEach(keys::remove);
      throw new IllegalStateException(
          String.format("Error: dangling steps: '%s'.  Please connect them", keys));
    }
  }

  public interface Step<K, V extends Component> {

    /**
     * @return the key for this step
     */
    K getKey();

    /**
     * @return the title for this step
     */
    String getTitle();

    /**
     * @return the icon factory for this step
     */
    IconFactory getIconFactory();

    /**
     * @return the component for this wizard
     */
    Class<V> getPage();
  }

  public interface TransitionListener<K, V> {

    /**
     * determine if the wizard can transition between this state and the next state
     *
     * @param state       the current state
     * @param host        the current wizard
     * @param currentPage the current wizard page
     * @return true if the transition can be made
     */
    default boolean canTransition(K state, Wizard<K, V> host, Component currentPage) {
      return true;
    }

    /**
     * fired before the wizard makes the transition
     *
     * @param state       the current state
     * @param host        the current wizard
     * @param currentPage the current page
     */
    default void beforeTransition(K state, Wizard<K, V> host, Component currentPage) {
    }

    /**
     * fired after the wizard makes the transition
     *
     * @param state       the current state
     * @param host        the current wizard
     * @param currentPage the current page
     */
    default void afterTransition(K state, Wizard<K, V> host, Component currentPage) {
    }
  }

  private static final class WizardStep<K, V extends Component> {

    private final K key;
    private final Class<V> page;
    private final String title;

    private V component;
    private IconFactory iconFactory;

    WizardStep(
        @NonNull final K key,
        @NonNull String title,
        @NonNull final Class<V> page,
        Class<? extends IconFactory> iconFactory) {
      this.key = key;
      this.page = page;
      this.title = title;
      if (!IconFactory.class.equals(iconFactory)) {
        this.iconFactory = Reflect.instantiate(iconFactory);
      } else {
        this.iconFactory = VaadinIcon.LIST::create;
      }
    }

    public WizardStep(K key, String title, Class<V> component, IconFactory iconFactory) {
      this.key = key;
      this.title = title;
      this.page = component;
      this.iconFactory = iconFactory;
    }

    public void setComponent(V component) {
      if (iconFactory == null && component instanceof IconFactory) {
        iconFactory = (IconFactory) component;
      }
      this.component = component;
    }
  }

  private static final class Transition<K, V> {

    private final K key;
    private final TransitionListener<K, V> listener;

    Transition(@NonNull final K key, @NonNull TransitionListener<K, V> listener) {
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

    public Step<K, V> icon(IconFactory iconSupplier) {
      return new DefaultStep<K, V>(key, title, pageType, iconSupplier);
    }
  }

  private static class DefaultStep<K, V extends Component> implements Step<K, V> {

    private final K key;
    private final String title;
    private final Class<V> pageType;
    private final IconFactory iconSupplier;

    public DefaultStep(K key, String title, Class<V> pageType, IconFactory iconSupplier) {
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
    public IconFactory getIconFactory() {
      return iconSupplier;
    }

    @Override
    public Class<V> getPage() {
      return pageType;
    }
  }
}
