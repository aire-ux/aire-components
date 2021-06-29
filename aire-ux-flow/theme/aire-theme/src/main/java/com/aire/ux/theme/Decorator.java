package com.aire.ux.theme;

import com.vaadin.flow.component.HasElement;

public interface Decorator {

  <T extends HasElement> T decorate(T value);
}
