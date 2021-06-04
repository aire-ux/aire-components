package com.aire.ux.test.vaadin;

import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import java.lang.reflect.AnnotatedElement;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import lombok.extern.java.Log;
import lombok.val;

@Log
final class TestFrame implements AutoCloseable {

  private String location;
  private final AtomicBoolean alive;
  private final RoutesCreator creator;
  private final AtomicReference<Routes> routes;

  TestFrame(RoutesCreator creator) {
    this.creator = creator;
    this.alive = new AtomicBoolean(true);
    this.routes = new AtomicReference<>();
  }

  static Iterable<ElementResolverFactory> resolverFactories() {
    return ServiceLoader.load(ElementResolverFactory.class, Thread.currentThread()
        .getContextClassLoader());
  }

  void activate() {
    checkLiveness();
    log.log(Level.INFO, "Activating Stack Frame %s...".formatted(this));
    routes.set(creator.create());
    MockVaadin.setup(routes());
    log.log(Level.INFO, "Activated Stack Frame %s".formatted(this));
  }

  void deactivate() {
    checkLiveness();
    log.log(Level.INFO, "Deactivating Stack Frame %s...".formatted(this));
    MockVaadin.tearDown();
    routes.set(null);
    log.log(Level.INFO, "Deactivated Stack Frame %s".formatted(this));
  }

  @Override
  public void close() {
    deactivate();
  }

  protected final Routes routes() {
    val result = this.routes.get();
    if (result == null) {
      throw new IllegalStateException(
          "Error: TestFrame context is being used in an invalid way (are we missing a frame?)");
    }
    return result;
  }

  private void checkLiveness() {
    if (!alive.get()) {
      throw new IllegalStateException("Error: Test Frame %s is not alive".formatted(this));
    }
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
        return resolverFactory.create(element);
      }
    }
    throw new NoSuchElementException(
        "No ElementResolverFactory applicable to type: %s".formatted(element));
  }

  public void navigateTo(String navigation) {
    this.location = navigation;
    UI.getCurrent().navigate(navigation);
  }

  public void restore() {
    if(location != null) {
      UI.getCurrent().navigate(location);
    }
  }
}
