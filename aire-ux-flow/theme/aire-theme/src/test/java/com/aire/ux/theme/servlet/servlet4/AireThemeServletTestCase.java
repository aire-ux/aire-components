package com.aire.ux.theme.servlet.servlet4;

import com.aire.ux.Theme;
import com.aire.ux.test.AireTest;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;

@AireTest
@EnableAireThemeServlet
public class AireThemeServletTestCase {

  protected HttpServletResponse response;

  @Inject protected WebApplicationContext context;

  @Inject protected ServletContext servletContext;

  @Inject protected AireThemeResourceServlet servlet;

  @BeforeEach
  void setUp() {
    response = new MockHttpServletResponse();
  }

  @SneakyThrows
  protected String readContent(HttpServletResponse response) {
    return ((MockHttpServletResponse) response).getContentAsString();
  }

  @SneakyThrows
  protected String readResource(String s) {
    val outputStream = new ByteArrayOutputStream();
    Theme.class.getClassLoader().getResourceAsStream(s).transferTo(outputStream);
    return outputStream.toString("UTF-8");
  }

  @SneakyThrows
  protected HttpServletResponse invoke(HttpServletRequest request) {
    servlet.doGet(request, response);
    return response;
  }

  protected HttpServletRequest get(String uri) {
    return request("GET", uri);
  }

  protected HttpServletRequest request(String method, String uri) {
    return new MockHttpServletRequest(servletContext, method, uri);
  }
}
