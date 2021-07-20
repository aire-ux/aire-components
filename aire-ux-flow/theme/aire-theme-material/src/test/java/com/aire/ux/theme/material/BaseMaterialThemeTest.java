package com.aire.ux.theme.material;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.aire.ux.Theme;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.Context.Mode;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.test.themes.EnableThemes;
import com.aire.ux.theme.ThemeDefinition;
import com.aire.ux.theme.context.AireThemeManager;
import com.aire.ux.theme.context.ThemeChangeListener;
import com.aire.ux.theme.context.ThemeChangeListener.ThemeChangeEventType;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.JsonSerializer;
import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventType;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@EnableThemes
@ContextConfiguration(classes = Cfg.class)
class BaseMaterialThemeTest {

  @AfterEach
  void tearDown() {
    assertEquals(0, AireThemeManager.getListenerCount());
  }

  @AfterAll
  static void tearDownAll() {
    assertEquals(0, AireThemeManager.getListenerCount());
  }


  @ViewTest
  void ensureThemeServletServesResourcesCorrectlyAndMockIsIdempotent(
      @Context(mode = Mode.Spy) UI ui) {
    val listener = new ThemeChangeListener(() -> ui);
    val reg = AireThemeManager.addEventListener(listener, ThemeChangeEventType.ThemeChanged);
    AireThemeManager.setTheme(MaterialDarkTheme.class);
    verify(ui, times(1)).getPage();
    reg.remove();
  }

  @ViewTest
  void ensureThemeServletServesResourcesCorrectly(@Context(mode = Mode.Spy) UI ui) {
    val listener = new ThemeChangeListener(() -> ui) {
      @Override
      public void onEvent(EventType eventType, Event<Theme> event) {
        super.onEvent(eventType, event);
        pushThemeToClient(new ThemeDefinition(event.getTarget()));
      }
    };
    val reg = AireThemeManager.addEventListener(listener, ThemeChangeEventType.ThemeChanged);
    AireThemeManager.setTheme(MaterialDarkTheme.class);
    verify(ui, times(1)).getPage();
    reg.remove();
  }

  private void pushThemeToClient(ThemeDefinition themeDefinition) {
    val result = JsonSerializer.toJson(themeDefinition);
    System.out.println(result);
  }
}
