package com.aire.ux.spring.test.servlet;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class TestServlet implements Servlet, DisposableBean, InitializingBean {


  private Servlet mockServlet;
  private ServletConfig mockServletConfiguration;

  @Override
  public void init(ServletConfig config) throws ServletException {
    mockServlet.init(config);
  }

  @Override
  public ServletConfig getServletConfig() {
    return mockServlet.getServletConfig();
  }

  @Override
  public void service(ServletRequest req, ServletResponse res)
      throws ServletException, IOException {
    mockServlet.service(req, res);
  }

  @Override
  public String getServletInfo() {
    return mockServlet.getServletInfo();
  }

  @Override
  public void destroy() {
    mockServlet.destroy();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    mockServlet = mock(Servlet.class);
    mockServletConfiguration = mock(ServletConfig.class);
    lenient().doReturn(mockServletConfiguration).when(mockServlet).getServletConfig();
  }


}
