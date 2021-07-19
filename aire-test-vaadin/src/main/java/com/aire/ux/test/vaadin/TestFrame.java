package com.aire.ux.test.vaadin;

import static java.lang.String.format;

import com.aire.mock.MockService;
import com.aire.mock.SpyService;
import com.aire.ux.test.Context;
import com.aire.ux.test.Context.Mode;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.Utilities;
import com.aire.ux.test.VaadinServletFactory;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.extension.ExtensionContext;

@Log
public final class TestFrame implements AutoCloseable {

  private final AtomicBoolean alive;
  private final RoutesCreator creator;

  private final Set<Object> mocks;
  private final Set<Object> spies;
  @Getter private final ExtensionContext context;
  private final AtomicReference<Routes> routes;
  private final Map<Class<?>, Object> values;
  private final List<DecoratingElementResolver> resolvers;
  private String location;

  TestFrame(RoutesCreator creator, ExtensionContext context) {
    this.creator = creator;
    this.context = context;
    this.resolvers = new ArrayList<>();
    this.routes = new AtomicReference<>();
    this.values = new ConcurrentHashMap<>();
    this.alive = new AtomicBoolean(true);
    this.mocks = new HashSet<>();
    this.spies = new HashSet<>();
  }

  static Iterable<ElementResolverFactory> resolverFactories() {
    return ServiceLoader.load(
        ElementResolverFactory.class, Thread.currentThread().getContextClassLoader());
  }

  static Optional<VaadinServletFactory> servletFactory() {
    return ServiceLoader.load(
            VaadinServletFactory.class, Thread.currentThread().getContextClassLoader())
        .findFirst();
  }

  public <T> void put(Class<T> type, T element) {
    values.put(type, element);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    return (T) values.get(type);
  }

  void activate() {
    checkLiveness();
    log.log(Level.INFO, "Activating Stack Frame {0}...", this);
    routes.set(creator.create());
    val fopt = servletFactory();
    if (fopt.isPresent()) {
      val factory = fopt.get();
      val currentUI = decorateUI(factory.getUIFactory().get());
      put(UI.class, currentUI);
      MockVaadin.setup(() -> currentUI, factory.createServlet(getRoutes()).orElseThrow());
    } else {
      MockVaadin.setup(getRoutes());
    }
    activateResolvers();
    restore();
    log.log(Level.INFO, "Activated Stack Frame {0}", this);
  }

  void deactivate() {
    checkLiveness();
    deactivateResolvers();
    log.log(Level.INFO, "Deactivating test frame {0}...", this);
    MockVaadin.tearDown();

    resetMocks();
    resetSpies();
    values.clear();
    routes.set(null);
    log.log(Level.INFO, "Deactivated tet frame {0}", this);
  }

  @Override
  public void close() {
    deactivate();
    resetUI();
    resolvers.clear();
  }

  public boolean hasElementResolver(AnnotatedElement element) {
    for (val resolverFactory : resolverFactories()) {
      if (resolverFactory.appliesTo(element)) {
        return true;
      }
    }
    return false;
  }

  public ElementResolver getElementResolver(AnnotatedElement element) {
    for (val resolverFactory : resolverFactories()) {
      if (resolverFactory.appliesTo(element)) {
        return decorate(element, resolverFactory.create(element));
      }
    }
    throw new NoSuchElementException(
        format("No ElementResolverFactory applicable to type: %s", element));
  }

  private ElementResolver decorate(AnnotatedElement element, ElementResolver resolver) {
    val decoratingResolver = new DecoratingElementResolver(this, resolver, element);
    resolvers.add(decoratingResolver);
    return decoratingResolver;
  }

  public void navigateTo(String navigation) {
    log.log(Level.INFO, "{0} navigating to {1}", new Object[] {this, navigation});
    this.location = navigation;
    UI.getCurrent().navigate(navigation);
    log.log(Level.INFO, "{0} navigated to {1}", new Object[] {this, navigation});
  }

  public void restore() {
    if (location != null) {
      log.log(Level.INFO, "Navigating back to {0}", location);
      UI.getCurrent().navigate(location);
    }
  }

  @Override
  public String toString() {
    return format(
        "TestFrame[location: %s, routes: %s]", location == null ? "none" : location, routes);
  }

  protected final Routes getRoutes() {
    val result = this.routes.get();
    if (result == null) {
      throw new IllegalStateException(
          "Error: TestFrame context is being used in an invalid way (are we missing a frame?)");
    }
    return result;
  }

  private void checkLiveness() {
    if (!alive.get()) {
      throw new IllegalStateException(format("Error: Test Frame %s is not alive", this));
    }
  }

  private void activateResolvers() {
    for (val resolver : resolvers) {
      resolver.activate();
    }
  }

  private void deactivateResolvers() {
    for (val resolver : resolvers) {
      resolver.deactivate();
    }
  }

  private UI decorateUI(UI ui) {
    return Frames.getCurrentTestMethod()
        .map(this::getUIContextParameter)
        .map(ctx -> mockOrDecorate(ctx, ui))
        .orElse(ui);
  }

  private UI mockOrDecorate(Optional<Context> context, UI ui) {
    return context
        .flatMap(
            ctx -> {
              if (Utilities.isMode(ctx, Mode.Mock)) {
                return Optional.of(mockUI(ui));
              }
              if (Utilities.isMode(ctx, Mode.Spy)) {
                return Optional.of(spyUI(ui));
              }
              return Optional.<UI>empty();
            })
        .orElse(ui);
  }

  @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
  private UI spyUI(UI ui) {

    val services =
        ServiceLoader.load(SpyService.class, Thread.currentThread().getContextClassLoader());
    for (val service : services) {
      val result = service.apply(ui);
      spies.add(result);
      return result;
    }
    return ui;
  }

  private UI mockUI(UI ui) {
    throw new IllegalArgumentException("Error: UI cannot be mocked, only spied");
  }

  private Optional<Context> getUIContextParameter(Method method) {
    val params = method.getParameters();
    for (val param : params) {
      if (Reflect.isCompatible(UI.class, param.getType())
          && param.isAnnotationPresent(Context.class)) {
        return Optional.ofNullable(param.getAnnotation(Context.class));
      }
    }
    return Optional.empty();
  }

  private void resetUI() {}

  public <T> boolean hasSpy(T value) {
    return spies.contains(value);
  }

  public <T> T addSpy(T apply) {
    spies.add(apply);
    return apply;
  }

  public <T> void removeMock(T value) {
    if (!mocks.remove(value)) {
      log.log(Level.WARNING, "No mocked value: {0}", value);
    }
  }

  public <T> void removeSpy(T value) {
    if (!spies.remove(value)) {
      log.log(Level.WARNING, "No spied value: {0}", value);
    }
  }

  public <T> boolean hasMock(T value) {
    return mocks.contains(value);
  }

  public <T> void addMock(T result) {
    mocks.add(result);
  }

  @SuppressWarnings("unchecked")
  public <T> T resolveContextVariable(Class<T> contextClass, Mode mode) {
    if (mode == Mode.Mock) {
      if (Reflect.isCompatible(UI.class, contextClass)) {
        return (T) mockUI(null);
      }
      for (val mock : mocks) {
        if (mock != null) {
          if (Reflect.isCompatible(contextClass, mock.getClass())) {
            return (T) mock;
          }
        }
      }
    }

    if (mode == Mode.Spy) {
      for (val spy : spies) {
        if (spy != null) {
          if (Reflect.isCompatible(contextClass, spy.getClass())) {
            return (T) spy;
          }
        }
      }
    }

    val result = values.get(contextClass);
    if (result != null) {
      if (mode == Mode.Spy) {
        val r = spy(result);
        values.remove(contextClass);
        spies.add(r);
        return (T) r;
      }

      if (mode == Mode.Mock) {
        val r = mock(result);
        values.remove(contextClass);
        mocks.add(r);
        return (T) r;
      }
    }
    return (T) result;
  }

  @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
  private Object mock(Object result) {
    val services =
        ServiceLoader.load(MockService.class, Thread.currentThread().getContextClassLoader());
    for (val service : services) {
      return service.apply(result);
    }
    return result;
  }

  @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
  private Object spy(Object result) {
    val services =
        ServiceLoader.load(SpyService.class, Thread.currentThread().getContextClassLoader());
    for (val service : services) {
      return service.apply(result);
    }
    return result;
  }

  private void resetSpies() {

    val services =
        ServiceLoader.load(SpyService.class, Thread.currentThread().getContextClassLoader());
    for (val service : services) {
      for (val spy : spies) {
        service.deactivate(spy);
      }
    }
  }

  private void resetMocks() {
    val services =
        ServiceLoader.load(MockService.class, Thread.currentThread().getContextClassLoader());
    for (val service : services) {
      for (val mock : mocks) {
        service.deactivate(mock);
      }
    }
  }
}
