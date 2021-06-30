package com.aire.ux;

public interface ThemeResource {

  /** the type of the theme resource */
  enum Type {
    Stylesheet,
    Javascript,
  }

  /**
   * attempt to load this resource asynchronously
   *
   * @return whether this theme resource should be loaded asynchronously
   */
  boolean loadAsynchronously();
}
