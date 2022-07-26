package com.aire.ux.core.decorators;

import com.aire.ux.ComponentDecorator;
import java.util.Collection;

public class CompositeComponentDecorator extends AbstractCompositeComponentDecorator {

  public CompositeComponentDecorator(final Collection<? extends ComponentDecorator> components) {
    super(components::stream);
  }
}
