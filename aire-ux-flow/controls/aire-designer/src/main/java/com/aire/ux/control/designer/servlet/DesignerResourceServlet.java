package com.aire.ux.control.designer.servlet;

import javax.servlet.http.HttpServlet;

public class DesignerResourceServlet extends HttpServlet {
  private final DesignerConfiguration configuration;

  public DesignerResourceServlet() {
    this(DesignerConfiguration.load());

  }

  public DesignerResourceServlet(final DesignerConfiguration configuration) {
    this.configuration = configuration;
  }



}
