package com.aire.ux.control.designer.servlet;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.val;

public class DesignerResourceServlet extends HttpServlet {

  private final DesignerConfiguration configuration;

  public DesignerResourceServlet() {
    this(DesignerConfiguration.load());
  }

  public DesignerResourceServlet(@NonNull final DesignerConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    val inputStream = locate(request);
    if (inputStream == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    try {
      response.setStatus(HttpServletResponse.SC_OK);
      inputStream.transferTo(response.getOutputStream());
    } catch (IOException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Nullable
  private InputStream locate(HttpServletRequest request) {
    return configuration.getResourceAsStream(request.getRequestURI());
  }
}
