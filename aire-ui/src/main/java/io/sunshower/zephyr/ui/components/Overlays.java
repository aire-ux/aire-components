package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import io.sunshower.zephyr.aire.DynamicInstantiator;
import lombok.val;

public class Overlays {

  public static void clearHost(Component host) {
    host.getElement().setAttribute("aire-overlay-host", "true");
  }

  public static void createHost(Component host) {
    host.getElement().setAttribute("aire-overlay-host", "true");
  }

  public static Overlay open(
      Component host, Class<? extends Overlay> content, Object... additionalArgs) {
    val actualHost = getActualHost(host);
    return host.getUI()
        .flatMap(ui -> DynamicInstantiator.create(ui, content, additionalArgs))
        .map(
            overlay -> {
              actualHost.getElement().appendChild(overlay.getElement());
              return overlay;
            })
        .orElseThrow(
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
