package com.aire.ux.theme.servlet.servlet4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.theme.TestTheme;
import com.aire.ux.theme.context.AireThemeManager;
import com.aire.ux.theme.decorators.AireTestTheme;
import com.aire.ux.theme.decorators.scenario1.MainView;
import com.aire.ux.theme.decorators.scenario1.TestButton;
import javax.servlet.http.HttpServletResponse;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@AireTest
@Disabled
@EnableAireThemeServlet
@Routes(scanClassPackage = MainView.class)
@com.aire.ux.theme.decorators.TestTheme(AireTestTheme.class)
class AireThemeResourceServletTest extends AireThemeServletTestCase {

  @Test
  void ensureContextIsLoaded() {
    assertNotNull(context);
  }

  @Test
  void ensureParsingUriWithNoParamsProducesCorrectPath() {
    val request = get("/aire/theme/theme-manager");
    val result = AireThemeResourceServlet.parseUrl(request);
    assertEquals(3, result.pathSegments.size());
  }

  @Test
  void ensureGettingThemeManagerWorks() {
    val request = get("/aire/theme/theme-manager");
    val response = invoke(request);
    assertEquals(readResource("META-INF/aire-scripts/aire.iife.js"), readContent(response));
  }

  @Test
  void ensureLoadingThemeResourceJavascriptWorks() {
    AireThemeManager.setTheme(TestTheme.class);
    val request = get("/aire/theme/current/resource.js");
    val response = invoke(request);
    assertEquals("application/javascript", response.getContentType());
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    assertEquals(readResource("test-theme/resource.js"), readContent(response));
  }

  @Test
  void ensureLoadingStylesheetResourceWorks() {
    AireThemeManager.setTheme(TestTheme.class);
    val request = get("/aire/theme/current/test.css");
    val response = invoke(request);
    assertEquals("text/css", response.getContentType());
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    assertEquals(readResource("test-theme/styles/test.css"), readContent(response));
  }

  @ViewTest
  @Navigate("main")
  void ensureComponentIsStyled(@Select("aire-button.test-theme") TestButton button) {
    assertNotNull(button);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/", "", "nothere.js"})
  void ensureLoadingResourceWithNoPathReturnsNotFound(String suffix) {

    AireThemeManager.setTheme(TestTheme.class);
    val request = get("/aire/theme/current/" + suffix);
    val response = invoke(request);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }
}
