package io.sunshower.zephyr.ui;

import com.aire.ux.condensation.Condensation;
import com.vaadin.flow.dom.Element;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

public class Elements {

  static final Condensation condensation;

  static {
    condensation = Condensation.create("json");
  }

  public static <T> Serializable writeAll(Class<T> type, Collection<T> items) {
    try {
      return condensation.getWriter().writeAll(type, items);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static void inClient(String functionName, Element element, Serializable values) {
    element.callJsFunction(functionName, values);
  }
}
