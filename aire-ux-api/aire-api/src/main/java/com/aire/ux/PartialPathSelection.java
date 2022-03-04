package com.aire.ux;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.val;

class PartialPathSelection<T> implements PartialSelection<T> {

  private final String path;
  private final Class<T> type;

  private WeakReference<Referent<Class<T>>> cache;

  public PartialPathSelection(String path, Class<T> type) {
    this.path = path;
    this.type = type;
    this.cache = new WeakReference<>(new Referent<>());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PartialPathSelection<?> that = (PartialPathSelection<?>) o;

    if (!path.equals(that.path)) {
      return false;
    }
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    int result = path.hashCode();
    result = 31 * result + (type == null ? 0 : type.hashCode());
    return result;
  }

  private String normalize(String path) {
    if (path == null) {
      return null;
    }
    if (path.charAt(0) != ':') {
      return ":" + path;
    }
    return path;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean isHostedBy(Class<?> type) {
    if (cache != null) {
      var referent = cache.get();
      if (referent != null && Objects.equals(referent.value, type)) {
        return true;
      }
    }

    for (var c = type; !Objects.equals(c, Object.class); c = c.getSuperclass()) {
      val host = c.getAnnotation(Host.class);
      if (host != null && path.startsWith(normalize(host.value()))) {
        final Referent<Class<T>> referent;
        if (cache == null) {
          cache = new WeakReference<>(referent = new Referent<>());
        } else {
          referent = cache.get();
        }
        if (referent == null) {
          return false;
        }
        referent.value = (Class<T>) c;
        return true;
      }
    }
    return false;
  }

  @Override
  public Optional<ExtensionDefinition<T>> select(
      UserInterface ui, Supplier<UI> supplier, Extension<T> extension) {
    return new PathSelection<>(ui, path, type).select(supplier, extension);
  }

  @Override
  public Optional<ExtensionDefinition<T>> select(
      HasElement component, UserInterface userInterface, Extension<T> extension) {
    return new PathSelection<>(userInterface, component, path, type)
        .select(() -> supplierFor(component), extension);
  }

  @Override
  public String getSegment() {
    return path;
  }

  private UI supplierFor(HasElement component) {
    if (component instanceof Component) {
      return ((Component) component).getUI().orElseGet(UI::getCurrent);
    }
    return UI.getCurrent();
  }

  static class Referent<U> {

    volatile U value;
  }
}
