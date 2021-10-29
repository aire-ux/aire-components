package com.aire.ux.test.spring.servlet;

import java.io.IOException;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import lombok.val;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class DefaultClient implements Client {

  private final MockMvc mvc;
  private final AntPathMatcher matcher;
  private final ConfigurableWebApplicationContext context;

  @Inject
  public DefaultClient(ConfigurableWebApplicationContext context) {
    this.context = context;
    this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    this.matcher = new AntPathMatcher();
  }


  @Override
  public String get(String path) {
    val request = new MockHttpServletRequest();
    val response = new MockHttpServletResponse();
    val servlet = locate(path);

    try {
      request.setRequestURI(path);
      servlet.service(request, response);
      return response.getContentAsString();
    } catch (IOException | ServletException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private Servlet locate(String path) {
    val servlets = context.getBeansOfType(Servlet.class);
    for (val servlet : servlets.values()) {
      val type = servlet.getClass();
      if (type.isAnnotationPresent(WebServlet.class)) {
        val annotation = type.getAnnotation(WebServlet.class);
        for (val p : annotation.value()) {
          if (matcher.match(p, path)) {
            return servlet;
          }
        }
      }
    }
    throw new NoSuchElementException("No servlet at " + path);
  }

  @Override
  public void post(String path, String body) {

  }
}
