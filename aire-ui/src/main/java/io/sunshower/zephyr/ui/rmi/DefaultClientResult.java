package io.sunshower.zephyr.ui.rmi;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.experimental.Delegate;

final class DefaultClientResult<T> implements ClientResult<T> {

  @NonNull private final Class<T> type;

  @NonNull @Delegate private final PendingJavaScriptResult delegate;

  @Nullable private final Class<? extends Collection<? extends T>> collectionType;

  public DefaultClientResult(
      @Nullable Class<? extends Collection<? extends T>> collectionType,
      Class<T> type,
      PendingJavaScriptResult result) {
    this.type = type;
    this.delegate = result;
    this.collectionType = collectionType;
  }

  public DefaultClientResult(Class<T> type, PendingJavaScriptResult result) {
    this(null, type, result);
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public Optional<Class<? extends Collection<? extends T>>> getCollectionType() {
    return Optional.ofNullable(collectionType);
  }

  @Override
  public PendingJavaScriptResult getResult() {
    return delegate;
  }
}
