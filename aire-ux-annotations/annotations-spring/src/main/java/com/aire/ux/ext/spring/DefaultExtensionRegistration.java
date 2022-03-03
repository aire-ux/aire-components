package com.aire.ux.ext.spring;

import com.aire.ux.Extension;
import com.aire.ux.Registration;
import com.aire.ux.PartialSelection;

public class DefaultExtensionRegistration<T> implements Registration {

  private final Extension<T> extension;
  private final PartialSelection<?> selection;
  private final Runnable finalizer;

  public DefaultExtensionRegistration(
      PartialSelection<T> select, Extension<T> extension, Runnable finalizer) {
    this.selection = select;
    this.extension = extension;
    this.finalizer = finalizer;
  }

  @Override
  public void remove() {
    finalizer.run();
  }

  public Extension<?> getExtension() {
    return extension;
  }

  public PartialSelection<?> getSelection() {
    return selection;
  }

  @SuppressWarnings("unchecked")
  public void decorate(Object c) {
    extension.decorate((T) c);
  }
}
