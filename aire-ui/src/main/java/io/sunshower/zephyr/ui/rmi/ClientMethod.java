package io.sunshower.zephyr.ui.rmi;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.page.PendingJavaScriptResult;

public interface ClientMethod<T> {
  PendingJavaScriptResult invoke(HasElement element, Object... params);
}
