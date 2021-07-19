package com.aire.ux.theme.material;

import static org.mockito.Mockito.spy;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.test.themes.EnableThemes;
import com.aire.ux.theme.ThemeDefinition;
import com.aire.ux.theme.context.ThemeChangeListener;
import com.vaadin.flow.component.UI;
import lombok.val;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@EnableThemes
@ContextConfiguration(classes = Cfg.class)
class BaseMaterialThemeTest {

  @ViewTest
  void ensureThemeServletServesResourcesCorrectly(@Context UI ui) {
    val listener = new ThemeChangeListener(() -> spy(ui));
  }

  private void pushThemeToClient(ThemeDefinition themeDefinition) {}
}
