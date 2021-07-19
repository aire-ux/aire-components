package com.aire.ux.test;

import com.aire.ux.test.vaadin.TestFrame;

public interface ParameterDecorator {

  <T> T decorate(T value, Context context, TestFrame filter);

  default <T> void activate(T value, Context context, TestFrame frame) {}

  default <T> void deactivate(T value, Context context, TestFrame frame) {}
}
