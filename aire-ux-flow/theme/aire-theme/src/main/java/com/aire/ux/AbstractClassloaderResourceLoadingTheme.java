package com.aire.ux;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.val;

public abstract class AbstractClassloaderResourceLoadingTheme implements Theme {

  private final String id;
  private final WeakReference<ClassLoader> classloader;

  protected AbstractClassloaderResourceLoadingTheme(
      @Nonnull final String id, @Nonnull final ClassLoader classLoader) {
    this.id = Objects.requireNonNull(id);
    this.classloader = new WeakReference<>(Objects.requireNonNull(classLoader));
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public InputStream openResource(@Nonnull String path) {
    val cl = classloader.get();
    if (cl == null) {
      throw new IllegalStateException("Error: theme has been closed or unloaded");
    }
    val result = cl.getResourceAsStream(path);
    if (result == null) {
      throw new NoSuchElementException(
          String.format("no resource at '%s' found within this theme", path));
    }
    return result;
  }
}
