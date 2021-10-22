package com.aire.ux.theme.aire;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Routes;
import com.aire.ux.test.ViewTest;
import com.aire.ux.theme.ThemeDefinition;
import com.aire.ux.theme.decorators.scenario1.MainView;
import com.aire.ux.theme.servlet.servlet4.AireThemeServletTestCase;
import com.aire.ux.theme.servlet.servlet4.EnableAireThemeServlet;
import com.vaadin.flow.internal.JsonSerializer;
import lombok.val;

@AireTest
@EnableAireThemeServlet
@Routes(scanClassPackage = MainView.class)
class BaseAireThemeTest extends AireThemeServletTestCase {

  @ViewTest
  void ensureSettingThemeLoadsAllResources() {
    val json = JsonSerializer.toJson(new ThemeDefinition(new AireLightTheme()));
    System.out.println(json);
    //    AireThemeManager.addEventListener(
    //        new ThemeChangeListener() {
    //          @Override
    //          public void onEvent(EventType eventType, Event<Theme> event) {
    //            System.out.println(event);
    //            super.onEvent(eventType, event);
    //          }
    //        }, ThemeChangeEventType.ThemeChanged);
    //    AireThemeManager.setTheme(AireLightTheme.class);
  }
}
