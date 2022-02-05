package io.sunshower.zephyr.ui;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.val;

public interface ClientResult<T> extends PendingJavaScriptResult {

  static <T> ClientResult<T> create(Class<T> type, PendingJavaScriptResult result) {
    return new DefaultClientResult<T>(type, result);
  }

  Class<T> getType();

  PendingJavaScriptResult getResult();

  default CompletableFuture<T> toFuture() {
    val result = getResult();
    val future = new CompletableFuture<T>();

    result.then(value -> {
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
