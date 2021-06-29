package com.aire.ux.core.decorators;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import javax.annotation.Nonnull;

/**
 * component decorators are used to rewrite or transform a component hierarchy upon instantiation.
 * The component tree is not traversed multiple times
 *
 * <p>This also implements a lifecycle for traversal:
 */
public interface ComponentDecorator extends AutoCloseable {

  /**
   * called before this decorator is applied to descendants
   *
   * @param component the current component
   */
  default void onComponentEntered(@Nonnull Component component) {}

  /** @param component the current component */
  default void onComponentEntered(@Nonnull HasElement component) {}

  default void onComponentExited(@Nonnull Component component) {}

  /** @param component the current component */
  default void onComponentExited(@Nonnull HasElement component) {}

  /** @param component the component to decorate */
  default void decorate(@Nonnull Component component) {}

  /** @param component the component to decorate */
  default void decorate(@Nonnull HasElement component) {}

  /**
   * @param decorator the decorator to compose this one with. Decorators are applied in the order
   *     they're composed
   * @return a decorator that's the composite of this and the provided decorator
   */
  @Nonnull
  default ComponentDecorator compose(@Nonnull ComponentDecorator decorator) {
    return this;
  }

  default void close() {}
}
