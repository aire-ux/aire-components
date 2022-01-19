package io.sunshower.zephyr.ui.layout;

/**
 * determine if we need to swap out the primary icon
 */
public interface ApplicationLayoutDecorator {

  default void decorate(ApplicationLayout layout) {

  }
  default void undecorate(ApplicationLayout layout) {

  }
}
