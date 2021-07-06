package com.aire.ux.theme.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.theme.TestTheme;
import com.aire.ux.theme.context.ThemeChangeListener.ThemeChangeEventType;
import com.aire.ux.theme.decorators.AireThemeComponentDecoratorTest.Cfg;
import com.vaadin.flow.component.UI;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@ExtendWith(MockitoExtension.class)
class AireThemeManagerTest {

  @Spy private ThemeChangeListener listener;

  @AfterEach
  void verifyState() {
    assertEquals(0, AireThemeManager.getListenerCount());
  }

  @Test
  void ensureThemeIsDispatchedCorrectly() throws Exception {
    try (val registration =
        AireThemeManager.addEventListener(listener, ThemeChangeEventType.ThemeChanged)) {
      AireThemeManager.setTheme(TestTheme.class);
      verify(listener).onEvent(any(), any());
      AireThemeManager.clearTheme();
    }
  }

  @Test
  void ensureDisposingRegistrationWorks() {
    val registration =
        AireThemeManager.addEventListener(listener, ThemeChangeEventType.ThemeChanged);
    assertEquals(1, AireThemeManager.getListenerCount());
    registration.remove();
    assertEquals(0, AireThemeManager.getListenerCount());
  }

  @Test
  void ensurePageIsCalledWithInvokeJavascript() throws Exception {
    val page = spy(UI.getCurrent().getPage());
    val currentUI = spy(UI.getCurrent());
    try (val uiStatic = Mockito.mockStatic(UI.class)) {
      uiStatic.when(UI::getCurrent).thenReturn(currentUI);
      when(currentUI.getPage()).thenReturn(page);
      try (val registration =
          AireThemeManager.addEventListener(listener, ThemeChangeEventType.ThemeChanged)) {
        AireThemeManager.setTheme(TestTheme.class);
        verify(page, times(1)).executeJs(eq("Aire.ThemeManager.uninstallStyles()"), any());
        verify(page, times(1)).executeJs(eq("Aire.ThemeManager.installStyles($0)"), any());
        AireThemeManager.clearTheme();
      }
    }
  }
}
