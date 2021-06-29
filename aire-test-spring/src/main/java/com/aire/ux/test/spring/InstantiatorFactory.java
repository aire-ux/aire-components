package com.aire.ux.test.spring;

import com.vaadin.flow.di.Instantiator;

public interface InstantiatorFactory {

  Instantiator create(Instantiator delegate);
}
