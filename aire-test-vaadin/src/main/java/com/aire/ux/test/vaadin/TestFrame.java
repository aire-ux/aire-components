package com.aire.ux.test.vaadin;

import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.VaadinServletFactory;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import java.lang.reflect.AnnotatedElement;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ServiceLoader;
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

  @Getter
  private final ExtensionContext context;
  private final AtomicReference<Routes> routes;

  private String location;

  TestFrame(RoutesCreator creator, ExtensionContext context) {
    this.creator = creator;
    this.context = context;
    this.routes = new AtomicReference<>();
    this.alive = new AtomicBoolean(true);
  }

  static Iterable<ElementResolverFactory> resolverFactories() {
    return ServiceLoader.load(
        ElementResolverFactory.class, Thread.currentThread().getContextClassLoader());
  }

  static Optional<VaadinServletFactory> servletFactory() {
    return ServiceLoader.load(
        VaadinServletFactory.class, Thread.currentThread().getContextClassLoader()).findFirst();
  }

  void activate() {
    checkLiveness();
    log.log(Level.INFO, "Activating Stack Frame %s...".formatted(this));
    routes.set(creator.create());
    val fopt = servletFactory();
    if (fopt.isPresent()) {
      val factory = fopt.get();
      MockVaadin.setup(() -> factory.getUIFactory().get(),
          factory.createServlet(getRoutes()).orElseThrow());
    } else {
      MockVaadin.setup(getRoutes());
    }

    restore();
    log.log(Level.INFO, "Activated Stack Frame %s".formatted(this));
  }

  private UI resolveService(Object t, Object u) {
    return null;
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
    log.info("%s navigating to %s".formatted(this, navigation));
    this.location = navigation;
    UI.getCurrent().navigate(navigation);
    log.info("%s navigated to %s".formatted(this, navigation));
  }

  public void restore() {
    if (location != null) {
      log.info("Navigating back to %s".formatted(location));
      UI.getCurrent().navigate(location);
    }
  }

  @Override
  public String toString() {
    return "TestFrame[location: %s, routes: %s]"
        .formatted(location == null ? "none" : location, routes);
  }
}
