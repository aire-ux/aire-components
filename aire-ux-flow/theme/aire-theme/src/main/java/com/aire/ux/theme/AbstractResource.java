package com.aire.ux.theme;

import com.aire.ux.ThemeResource;
import javax.annotation.Nonnull;

public class AbstractResource implements ThemeResource {

  /** the type of this resource */
  private final Type type;

  /** the name within the theme of this resource */
  private final String name;

  /** attempt to load this resource asynchronously. Defaults to false */
  private final boolean async;

  /** the location within the theme of this resource. Usually relative to the classloader */
  private final String location;

  /** the mime-type of this resource, such as {@code application/javascript} */
  private final String mimeType;

  protected AbstractResource(
      @Nonnull final Type type,
      @Nonnull final String name,
      @Nonnull final boolean async,
      @Nonnull final String location,
      @Nonnull final String mimeType) {
    this.type = type;
    this.name = name;
    this.async = async;
    this.location = location;
    this.mimeType = mimeType;
  }

  protected AbstractResource(
      @Nonnull final Type type,
      @Nonnull final String name,
      @Nonnull final String location,
      @Nonnull final String mimeType) {
    this(type, name, false, location, mimeType);
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public String getMimeType() {
    return mimeType;
  }

  @Override
  public boolean loadAsynchronously() {
    return async;
  }
}
