package com.aire.ux.core.decorators;

import com.vaadin.flow.component.HasElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.val;

public class CompositeComponentDecorator implements ComponentDecorator {

  private final List<ComponentDecorator> delegates;

  public CompositeComponentDecorator(
      @Nonnull final Collection<? extends ComponentDecorator> delegates) {
    this.delegates = new ArrayList<>(delegates);
  }


  @Override
  public void onComponentEntered(@Nonnull HasElement component) {
    for(val delegate : delegates) {
      delegate.onComponentEntered(component);
    }
  }

  @Override
  public void onComponentExited(@Nonnull HasElement component) {
    for(val delegate : delegates) {
      delegate.onComponentExited(component);
    }
  }

  /** @param component the component to apply these decorators to */
  @Override
  public void decorate(@Nonnull HasElement component) {
    for (val delegate : delegates) {
      delegate.decorate(component);
    }
  }

  @Nonnull
  public ComponentDecorator compose(@Nonnull ComponentDecorator decorator) {
    delegates.add(decorator);
    return this;
  }

  @Override
  public void close() {
    delegates.clear();
  }
}
