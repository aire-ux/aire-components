package com.aire.ux.test.spring.servlet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import lombok.val;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
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
  @SuppressFBWarnings
  public String get(String path) {
    val request = new MockHttpServletRequest();
    val response = new MockHttpServletResponse();
    val servlet = locate(path);

    try {
      request.setRequestURI(path);
      request.setMethod("GET");
      Objects.requireNonNull(context.getServletContext())
          .getRequestDispatcher(path)
          .include(request, response);
      servlet.service(request, response);
      return response.getContentAsString();
    } catch (IOException | ServletException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private Servlet locate(String path) {
    val registrationBeans = context.getBeansOfType(ServletRegistrationBean.class);
    for (val bean : registrationBeans.values()) {
      for (val mapping : bean.getUrlMappings()) {
        if (matcher.match((String) mapping, path)) {
          return bean.getServlet();
        }
      }
    }
    throw new NoSuchElementException("No servlet at " + path);
  }

  @Override
  public void post(String path, String body) {}
}
