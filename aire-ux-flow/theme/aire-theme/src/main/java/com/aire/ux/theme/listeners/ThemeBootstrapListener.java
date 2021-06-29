package com.aire.ux.theme.listeners;

import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.BootstrapPageResponse;
import lombok.val;

public class ThemeBootstrapListener implements BootstrapListener {

  @Override
  public void modifyBootstrapPage(BootstrapPageResponse response) {
    val document = response.getDocument();
    val scriptTag = document.createElement("script");
    scriptTag.attr("type", "application/javascript");
    scriptTag.attr("async");
    scriptTag.attr("src", "/aire/theme/theme-manager");
    document.body().appendChild(scriptTag);
  }
}
