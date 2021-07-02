package com.aire.ux.theme.servlet.servlet4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.Theme;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.theme.TestTheme;
import com.aire.ux.theme.context.AireThemeManager;
import com.aire.ux.theme.decorators.scenario1.MainView;
import com.aire.ux.theme.decorators.scenario1.TestButton;
import com.aire.ux.theme.servlet.servlet4.AireThemeResourceServletTest.Config;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.context.WebApplicationContext;

@AireTest
@EnableSpring
@ExtendWith(MockitoExtension.class)
@Routes(scanClassPackage = MainView.class)
@SpringJUnitWebConfig(classes = Config.class)
class AireThemeResourceServletTest {

  private HttpServletResponse response;

  @Inject private WebApplicationContext context;

  @Inject private ServletContext servletContext;

  @Inject private AireThemeResourceServlet servlet;

  @BeforeEach
  void setUp() {
    response = new MockHttpServletResponse();
  }

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
    assertEquals(readResource("META-INF/aire-scripts/index.js"), readContent(response));
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

  @SneakyThrows
  private String readContent(HttpServletResponse response) {
    return ((MockHttpServletResponse) response).getContentAsString();
  }

  @SneakyThrows
  private String readResource(String s) {
    val outputStream = new ByteArrayOutputStream();
    Theme.class.getClassLoader().getResourceAsStream(s).transferTo(outputStream);
    return outputStream.toString("UTF-8");
  }

  @SneakyThrows
  private HttpServletResponse invoke(HttpServletRequest request) {
    servlet.doGet(request, response);
    return response;
  }

  private HttpServletRequest get(String uri) {
    return request("GET", uri);
  }

  private HttpServletRequest request(String method, String uri) {
    return new MockHttpServletRequest(servletContext, method, uri);
  }

  @Configuration
  public static class Config {

    @Bean
    @Scope("singleton")
    public HttpServlet servlet() {
      return new AireThemeResourceServlet();
    }

    @Bean
    @Scope("singleton")
    public ServletRegistrationBean<AireThemeResourceServlet> aireUXScriptManagerServlet() {
      return new ServletRegistrationBean<>(new AireThemeResourceServlet(), "/aire/theme");
    }
  }
}
