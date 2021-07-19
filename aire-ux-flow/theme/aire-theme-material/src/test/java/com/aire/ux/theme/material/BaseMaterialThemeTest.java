package com.aire.ux.theme.material;

import static org.mockito.Mockito.spy;

import com.aire.ux.Theme;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.test.themes.EnableThemes;
import com.aire.ux.theme.ThemeDefinition;
import com.aire.ux.theme.context.AireThemeManager;
import com.aire.ux.theme.context.ThemeChangeListener;
import com.vaadin.flow.component.UI;
import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventType;
import lombok.val;
import org.junit.jupiter.api.Test;

@AireTest
@EnableSpring
@EnableThemes
class BaseMaterialThemeTest {


  @Test
  void ensureThemeServletServesResourcesCorrectly(@Context UI ui) {
    val listener = new ThemeChangeListener(() -> spy(ui));
  }

  private void pushThemeToClient(ThemeDefinition themeDefinition) {

  }


}