package com.aire.ux;

public interface ThemeResource {

  /** the type of the theme resource */
  enum Type {
    Stylesheet,
    Javascript,
  }

  Type getType();

  String getName();
  /** @return the theme-relative location of this resource, accessible via openResource() */
  String getLocation();

  /** @return the mimetype of this resource */
  String getMimeType();

  /**
   * attempt to load this resource asynchronously
   *
   * @return whether this theme resource should be loaded asynchronously
   */
  boolean loadAsynchronously();
}
