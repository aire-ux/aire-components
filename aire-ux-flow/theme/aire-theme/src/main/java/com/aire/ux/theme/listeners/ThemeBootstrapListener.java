package com.aire.ux.theme.listeners;

import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.BootstrapPageResponse;
import lombok.val;

public class ThemeBootstrapListener implements BootstrapListener {

  /** the path that this listener expects theme-manager.js to be at */
  public static final String THEME_MANAGER_PATH = "/aire/theme/theme-manager";

  @Override
  public void modifyBootstrapPage(BootstrapPageResponse response) {
    includeScriptManagerTag(response);
  }

  private void includeScriptManagerTag(BootstrapPageResponse response) {
    val document = response.getDocument();
    val scriptTag = document.createElement("script");
    scriptTag.attr("type", "application/javascript");
    scriptTag.attr("async");
    scriptTag.attr("src", THEME_MANAGER_PATH);
    document.body().appendChild(scriptTag);
  }
}
