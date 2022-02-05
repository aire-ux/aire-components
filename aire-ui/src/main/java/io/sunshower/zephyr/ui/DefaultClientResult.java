package io.sunshower.zephyr.ui;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import lombok.NonNull;
import lombok.experimental.Delegate;

final class DefaultClientResult<T> implements ClientResult<T> {

  @NonNull private final Class<T> type;

  @NonNull @Delegate private final PendingJavaScriptResult delegate;

  public DefaultClientResult(Class<T> type, PendingJavaScriptResult result) {
    this.type = type;
    this.delegate = result;
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public PendingJavaScriptResult getResult() {
    return delegate;
  }
}
