package io.sunshower.zephyr.ui;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.sunshower.zephyr.ui.canvas.ClientResult;
import lombok.NonNull;
import lombok.experimental.Delegate;

final class DefaultClientResult<T> implements ClientResult<T> {

  @NonNull
  @Delegate
  final PendingJavaScriptResult delegate;

  DefaultClientResult(@NonNull PendingJavaScriptResult delegate) {
    this.delegate = delegate;
  }

}
