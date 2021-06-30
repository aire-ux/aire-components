package com.aire.ux.theme;

import static java.util.Objects.requireNonNull;

import com.aire.ux.ThemeResource;
import javax.annotation.Nonnull;

public class JavascriptResource implements ThemeResource {

  private final String name;
  private final boolean async;
  private final String location;

  public JavascriptResource(@Nonnull String location, @Nonnull String name) {
    this(true, location, name);
  }

  public JavascriptResource(
      final boolean async, @Nonnull final String location, @Nonnull final String name) {
    this.async = async;
    this.name = requireNonNull(name);
    this.location = requireNonNull(location);
  }

  @Override
  public Type getType() {
    return Type.Javascript;
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
    return "application/javascript";
  }

  @Override
  public boolean loadAsynchronously() {
    return async;
  }
}
