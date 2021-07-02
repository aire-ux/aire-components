package com.aire.ux.test;

import com.vaadin.flow.di.Instantiator;

public interface InstantiatorFactory {

  Instantiator create(Instantiator delegate);
}
