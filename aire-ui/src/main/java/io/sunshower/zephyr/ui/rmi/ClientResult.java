package io.sunshower.zephyr.ui.rmi;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.val;

public interface ClientResult<T> extends PendingJavaScriptResult {

  static <T> ClientResult<T> create(Class<T> type, PendingJavaScriptResult result) {
    return new DefaultClientResult<T>(type, result);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static <T> ClientResult<List<T>> createList(Class<T> type, PendingJavaScriptResult invoke) {
    val cl = (Class) List.class;
    return new DefaultClientResult<List<T>>(cl, (Class) type, invoke);
  }

  Class<T> getType();

  Optional<Class<? extends Collection<? extends T>>> getCollectionType();

  PendingJavaScriptResult getResult();

  default boolean isCollectionType() {
    return getCollectionType().isPresent();
  }

  default CompletableFuture<T> toFuture() {
    val result = getResult();
    val future = new CompletableFuture<T>();

    result.then(
        value -> {
          val wrapped = CondensationUtilities.wrap(getType(), value);
          future.complete(wrapped);
        });
    return future;
  }

  default T block() {
    try {
      return toFuture().get();
    } catch (InterruptedException | ExecutionException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
