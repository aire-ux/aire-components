package com.aire.ux.theme.decorators;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.Theme;
import com.aire.ux.ThemeResource;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.theme.context.ThemeContext;
import com.aire.ux.theme.context.ThemeContextHolderStrategy;
import com.aire.ux.theme.decorators.AireThemeComponentDecoratorTest.Cfg;
import com.aire.ux.theme.decorators.AireThemeComponentDecoratorTest.TestThemeStrategy;
import com.aire.ux.theme.decorators.scenario1.MainView;
import com.aire.ux.theme.decorators.scenario1.TestButton;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasStyle;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

/**
 * this needs to come first, unfortunately
 */
@WithTheme(strategyClass = TestThemeStrategy.class)
@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@Routes(scanClassPackage = MainView.class)
class AireThemeComponentDecoratorTest {

  @ViewTest
  void ensureTestButtonIsDecorated(@Select("vaadin-button.test-theme") TestButton button) {
    assertNotNull(button);
  }

  @Configuration
  public static class Cfg {}

  public static class TestThemeStrategy implements ThemeContextHolderStrategy, ThemeContext, Theme {

    @Override
    public void clearContext() {}

    @Override
    public ThemeContext createThemeContext() {
      return this;
    }

    @Override
    public ThemeContext getContext() {
      return this;
    }

    @Override
    public void setContext(ThemeContext context) {}

    @NotNull
    @Override
    public Theme getTheme() {
      return this;
    }

    @Override
    public void setTheme(@NotNull Theme theme) {}

    @Override
    public String getId() {
      return "test-theme";
    }

    @Override
    public InputStream openResource(String path) {
      return getClass().getClassLoader().getResourceAsStream(path);
    }

    @Override
    public List<ThemeResource> getThemeResources() {
      return Collections.emptyList();
    }

    @Override
    public <T extends HasElement> void apply(T value) {
      if (value instanceof HasStyle) {
        ((HasStyle) value).setClassName("test-theme");
      }
    }
  }
}
