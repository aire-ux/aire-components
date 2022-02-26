package com.aire.ux.annotations;

import com.aire.ux.core.decorators.CompositeComponentDecorator;
import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.aire.ux.ext.ExtensionComponentDecorator;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.test.InstantiatorFactory;
import com.vaadin.flow.di.Instantiator;
import java.util.List;
import lombok.val;

public class TestInstantiatorFactory implements InstantiatorFactory {

  public Instantiator create(Instantiator delegate) {

    val delegates =
        new CompositeComponentDecorator(
            List.of(
                new ExtensionComponentDecorator(delegate.getOrCreate(ExtensionRegistry.class)),
                new ServiceLoaderComponentDecorator()));
    return new BaseAireInstantiator(delegate, delegates);
  }
}
