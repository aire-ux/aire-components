package io.sunshower.zephyr.ui.components;

import static java.lang.String.format;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
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
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.zephyr.aire.DynamicInstantiator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

@Tag("aire-wizard")
@JsModule("./aire/ui/components/wizard.ts")
@CssImport("./styles/aire/ui/components/wizard.css")
public class Wizard<K> extends HtmlContainer {

  static final TransitionListener<?> NO_OP = new TransitionListener<>() {
  };
  /**
   * immutable state
   */
  private final Nav header;
  private final WizardModel<K> model;
  private final Map<K, WizardStep<K, ?>> steps;

  private final Deque<WizardStep<K, ?>> history;
  private final Map<K, Transition<K>> transitions;

  /**
   * mutable state
   */
  private WizardStep<K, ?> currentStep;

  public Wizard() {
    model = WizardModel.newModel();
    steps = new HashMap<>();
    history = new ArrayDeque<>();
    transitions = new HashMap<>();
    header = createHeader();
    add(header);
  }


  @SuppressWarnings("unchecked")
  public <U extends Enum<U>> Wizard(Class<U> type) {
    model = (WizardModel<K>) WizardModel.newModel(type);
    steps = new HashMap<>();
    history = new ArrayDeque<>();
    transitions = new HashMap<>();
    header = createHeader();
    add(header);
  }


  public static <K> WizardKeyStepBuilder<K> key(K key) {
    return new WizardKeyStepBuilder(key);
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
  public void addTransition(K from, K to, TransitionListener<K> listener) {
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
    addTransition(from, to, (TransitionListener<K>) NO_OP);
  }

  @SuppressWarnings("unchecked")
  public <T extends Component, U extends Component> void addTransition(Class<T> from, Class<U> to) {
    addTransition(from, to, (TransitionListener<K>) NO_OP);
  }

  /**
   * @param from     the annotated source state
   * @param to       the annotated target state
   * @param listener the listener to bind to the transition
   * @param <T>      the type-parameter of the source state
   * @param <U>      the type-parameter of the target state
   */
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


  public K retreat() {
    if(!canRetreat()) {
      throw new IllegalStateException("Error: no previous states!");
    }

    val previous = history.pop();
    setCurrentStep(previous);
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


  protected final List<StepDescriptor<K>> getSteps() {
    if (currentStep == null) {
      throw new IllegalStateException("No initial step");
    }
    var current = currentStep.key;
    val results = new ArrayList<StepDescriptor<K>>();
    while (current != null) {
      val step = steps.get(current);
      val descriptor = new StepDescriptor<>(step);
      results.add(descriptor);

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

  protected void createProgressView(List<StepDescriptor<K>> steps) {
    val list = new UnorderedList();
    list.getElement().setAttribute("part", "wizard-list");
    for (val step : steps) {
      val listItem = new ListItem();
      listItem.add(step.getIcon().create());
      val title = new Span(new Text(step.getTitle()));
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

  private void setCurrentStep(WizardStep<K, ?> step) {
    updatePage(currentStep, step);
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
          "Error: page type '%s' must be annotated with @WizardPage");
    }
    return new WizardStep<>(
        (K) pageDefinition.key(), pageDefinition.title(), page, pageDefinition.iconFactory());
  }

  @SuppressWarnings("unchecked")
  private void updatePage(WizardStep<K, ?> previous, WizardStep<K, ?> step) {
    access(() -> {
      if(!(previous == null || previous.component == null)) {
        remove(previous.component);
      }
      val page = instantiate(step.page);
      page.getElement().setAttribute("slot", "page");
      ((WizardStep<K, Component>) step).component = page;
      add(page);
      currentStep = step;
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
          String.format("Error: dangling steps: '%s'.  Please connect them", allSteps));
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

  public interface TransitionListener<K> {

    /**
     * determine if the wizard can transition between this state and the next state
     *
     * @param state       the current state
     * @param host        the current wizard
     * @param currentPage the current wizard page
     * @return true if the transition can be made
     */
    default boolean canTransition(K state, Wizard<K> host, Component currentPage) {
      return true;
    }

    /**
     * fired before the wizard makes the transition
     *
     * @param state       the current state
     * @param host        the current wizard
     * @param currentPage the current page
     */
    default void beforeTransition(K state, Wizard<K> host, Component currentPage) {
    }

    /**
     * fired after the wizard makes the transition
     *
     * @param state       the current state
     * @param host        the current wizard
     * @param currentPage the current page
     */
    default void afterTransition(K state, Wizard<K> host, Component currentPage) {
    }
  }

  @Getter
  public static final class StepDescriptor<K> {

    private final K key;
    private final String title;
    private final IconFactory icon;

    public StepDescriptor(K key, String title,
        IconFactory icon) {
      this.key = key;
      this.title = title;
      this.icon = icon;
    }

    public StepDescriptor(WizardStep<K, ?> step) {
      this(step.key, step.title, step.iconFactory);
    }
  }

  private static final class WizardStep<K, V extends Component> {

    private final K key;
    private final Class<V> page;
    private final String title;
    private final IconFactory iconFactory;

    private V component;

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
