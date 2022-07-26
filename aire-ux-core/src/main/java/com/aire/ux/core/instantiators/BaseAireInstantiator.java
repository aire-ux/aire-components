package com.aire.ux.core.instantiators;

import com.aire.ux.ComponentDecorator;
import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.DependencyFilter;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Flushable;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;

public class BaseAireInstantiator implements Instantiator, Flushable {

  @Getter(AccessLevel.PROTECTED)
  private final Instantiator delegate;

  private final ComponentDecorator decorator;

  /**
   * Creates a new instantiator for the given service.
   *
   * @param delegate the base instantiator to use
   * @param decorator the decorator to use
   */
  public BaseAireInstantiator(
      @Nonnull Instantiator delegate, @Nonnull ComponentDecorator decorator) {
    this.delegate = Objects.requireNonNull(delegate);
    this.decorator = Objects.requireNonNull(decorator);
  }

  /** @param delegate */
  public BaseAireInstantiator(@Nonnull Instantiator delegate) {
    this(
        delegate,
        new ServiceLoaderComponentDecorator(Thread.currentThread().getContextClassLoader()));
  }

  protected <T> T doGetOrCreate(Class<T> type) {
    return delegate.getOrCreate(type);
  }

  @Override
  public void flush() {
    Optional.ofNullable(UI.getCurrent())
        .map(UI::getElement)
        .flatMap(Element::getComponent)
        .ifPresent(this::decorate);
  }

  @Override
  public boolean init(VaadinService service) {
    return delegate.init(service);
  }

  @Override
  public Stream<VaadinServiceInitListener> getServiceInitListeners() {
    return delegate.getServiceInitListeners();
  }

  @Override
  public <T> T getOrCreate(Class<T> type) {
    val result = doGetOrCreate(type);
    preDecorateResult(type, result);
    if (HasElement.class.isAssignableFrom(type)) {
      decorate((HasElement) result);
    }
    postDecorateResult(type, result);
    return result;
  }

  @Override
  public <T extends Component> T createComponent(Class<T> componentClass) {
    val result = doCreateComponent(componentClass);
    preDecorateResult(componentClass, result);
    decorate(result);
    postDecorateResult(componentClass, result);
    return result;
  }

  protected <T extends Component> T doCreateComponent(Class<T> componentClass) {
    return delegate.createComponent(componentClass);
  }

  protected <T extends HasElement> T doCreateRouteTarget(
      Class<T> routeTargetType, NavigationEvent event) {
    return delegate.createRouteTarget(routeTargetType, event);
  }

  @Override
  public Stream<BootstrapListener> getBootstrapListeners(
      Stream<BootstrapListener> serviceInitListeners) {
    return delegate.getBootstrapListeners(serviceInitListeners);
  }

  @Override
  public Stream<DependencyFilter> getDependencyFilters(
      Stream<DependencyFilter> serviceInitFilters) {
    return delegate.getDependencyFilters(serviceInitFilters);
  }

  @Override
  public <T extends HasElement> T createRouteTarget(
      Class<T> routeTargetType, NavigationEvent event) {
    val result = doCreateRouteTarget(routeTargetType, event);
    decorateRouteTarget(routeTargetType);
    decorate(result);
    return result;
  }

  @Override
  public I18NProvider getI18NProvider() {
    return delegate.getI18NProvider();
  }

  /**
   * extension point
   *
   * @param componentClass the type of the current component
   * @param result the result
   * @param <T> the type parameter
   */
  protected <T> void preDecorateResult(Class<T> componentClass, T result) {}

  /**
   * extension point
   *
   * @param componentClass the type of the current component
   * @param result the result
   * @param <T> the type parameter
   */
  protected <T> void postDecorateResult(Class<T> componentClass, T result) {}

  protected <T extends HasElement> void decorateRouteTarget(Class<T> routeTargetType) {}

  @SuppressFBWarnings
  private <T extends HasElement> void decorate(T result) {
    val stack = new ArrayDeque<Frame>();
    var current = new Frame(0, result);
    stack.push(current);

    while (!stack.isEmpty()) {
      current = stack.pop();
      val c = current.current;
      switch (current.stage) {
        case 0:
          {
            decorator.onComponentEntered(c);
            decorator.decorate(c);
            current.stage = 1;
            stack.push(current);
            val el = c.getElement();
            for (int i = 0; i < el.getChildCount(); i++) {
              val childOpt = el.getChild(i).getComponent();
              childOpt.ifPresent(component -> stack.push(new Frame(0, component)));
            }
            continue;
          }
        case 1:
          {
            decorator.onComponentExited(c);
            break;
          }
      }
    }
  }

  /**
   * not sure if we're at risk for stack overflow errors here but this should really be an inline
   * type
   */
  @NoArgsConstructor
  @AllArgsConstructor
  static final class Frame {

    private int stage;
    private HasElement current;
  }
}
