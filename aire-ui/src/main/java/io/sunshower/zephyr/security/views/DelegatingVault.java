package io.sunshower.zephyr.security.views;

import io.sunshower.crypt.core.Vault;
import lombok.NonNull;
import lombok.experimental.Delegate;

public class DelegatingVault implements Vault {

  @Delegate private final Vault delegate;

  public DelegatingVault(@NonNull final Vault delegate) {
    this.delegate = delegate;
  }
}
