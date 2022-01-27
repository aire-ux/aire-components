package com.aire.ux.core.decorators;

import com.vaadin.flow.component.HasElement;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public class AbstractCompositeComponentDecorator implements ComponentDecorator {

  private final Supplier<Stream<? extends ComponentDecorator>> delegateFactory;

  protected AbstractCompositeComponentDecorator(
      Supplier<Stream<? extends ComponentDecorator>> delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public void onComponentEntered(@Nonnull HasElement component) {
    delegates().forEach(c -> c.onComponentEntered(component));
  }

  @Override
  public void onComponentExited(@Nonnull HasElement component) {
    delegates().forEach(c -> c.onComponentExited(component));
  }

  /** @param component the component to apply these decorators to */
  @Override
  public void decorate(@Nonnull HasElement component) {
    delegates().forEach(c -> c.decorate(component));
  }

  @Nonnull
  public ComponentDecorator compose(@Nonnull ComponentDecorator decorator) {
    return new AbstractCompositeComponentDecorator(
        () ->
            Stream.concat(
                StreamSupport.stream(delegateFactory.get().spliterator(), false),
                Stream.of(decorator)));
  }

  @Override
  public void close() {}

  protected Stream<? extends ComponentDecorator> delegates() {
    return delegateFactory.get();
  }
}
