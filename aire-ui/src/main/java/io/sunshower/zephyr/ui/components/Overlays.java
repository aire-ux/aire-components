package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.di.Instantiator;
import lombok.val;

public class Overlays {

  public static void clearHost(Component host) {
    host.getElement().setAttribute("aire-overlay-host", "true");
  }

  public static void createHost(Component host) {
    host.getElement().setAttribute("aire-overlay-host", "true");
  }

  public static Overlay open(Component host, Class<? extends Overlay> content) {
    val actualHost = getActualHost(host);
    val result =
        host.getUI()
            .map(Instantiator::get)
            .map(instantiator -> instantiator.createComponent(content));
    result.ifPresent(overlay -> actualHost.getElement().appendChild(overlay.getElement()));
    return result.orElseThrow(
        () -> new IllegalArgumentException("Failed to create overlay--did you set a host?"));
  }

  private static Component getActualHost(Component host) {
    var result = host;
    while (result != null && !result.getElement().hasAttribute("aire-overlay-host")) {
      result =
          result
              .getParent()
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "No host in hierarchy! (Did you forget to call createHost?)"));
    }
    return result;
  }
}
