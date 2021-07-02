package com.aire.ux.theme.servlet.servlet4;

import com.aire.ux.Theme;
import com.aire.ux.ThemeResource;
import com.aire.ux.theme.context.AireThemeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.val;

/**
 * This servlet is a bit overloaded, but its functionality is as follows:
 *
 * <p>1. Request to `http://server/aire/theme/theme-manager` results in the theme-manager.js script
 * contents
 *
 * <p>2. Request to `http://server/aire/theme/current/[resource].ext results in a. If the current
 * theme is null or unavailable, a 404 b. Otherwise, lookup the content-type via {@code ext} c. Set
 * the content-type in the response d. Write the contents from the theme's classloader to the
 * outputstream
 */
@WebServlet(urlPatterns = "/aire/theme/")
public class AireThemeResourceServlet extends HttpServlet {

  private static String parsePathWithoutContext(HttpServletRequest request) {
    val uri = request.getRequestURI();
    val pathIdx = uri.startsWith("/") ? 1 : 0;
    return uri.substring(
        request.getContextPath().length() + request.getServletPath().length() + pathIdx);
  }

  static UrlComponents parseUrl(HttpServletRequest request) {
    val pathWithoutContext = parsePathWithoutContext(request);
    val result = new UrlComponents();
    result.pathSegments.addAll(Arrays.asList(pathWithoutContext.split("/")));
    val queryString = request.getQueryString();
    if (queryString != null) {
      for (val queryParam : queryString.split("&")) {
        val value = queryParam.split("=");
        result.queryParams.computeIfAbsent(queryParam, k -> new ArrayList<>()).add(value[1]);
      }
    }
    return result;
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    val components = parseUrl(request);
    if (components.hasSegment("theme-manager")) {
      writeScriptManager(response);
    }
    if (components.isResource()) {
      val resource = getResource(components);
      if (resource == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
      writeResource(resource, response);
    }
  }

  private void writeResource(ThemeResource resource, HttpServletResponse response)
      throws IOException {
    val theme = AireThemeManager.getCurrent();
    response.setContentType(resource.getMimeType());
    response.setStatus(HttpServletResponse.SC_OK);
    try (val stream = theme.openResource(resource.getLocation())) {
      Objects.requireNonNull(stream).transferTo(response.getOutputStream());
    }
  }

  private ThemeResource getResource(UrlComponents components) {
    val idx = components.pathSegments.indexOf("current");
    if (idx == -1) {
      return null;
    }

    if (idx + 1 < components.pathSegments.size()) {
      val theme = AireThemeManager.getCurrent();
      val resource = components.pathSegments.get(idx + 1);
      return theme.getThemeResource(resource);
    }
    return null;
  }

  private void writeScriptManager(HttpServletResponse response) throws IOException {
    response.setContentType("application/javascript");
    response.setStatus(HttpServletResponse.SC_OK);
    try (val stream = Theme.class.getResourceAsStream("/META-INF/aire-scripts/aire.iife.js")) {
      Objects.requireNonNull(stream).transferTo(response.getOutputStream());
    }
  }

  static class UrlComponents {

    final List<String> pathSegments;
    final Map<String, List<String>> queryParams;

    public UrlComponents() {
      queryParams = new HashMap<>();
      pathSegments = new ArrayList<>();
    }

    public boolean hasSegment(String s) {
      for (val seg : pathSegments) {
        if (seg.equals(s)) {
          return true;
        }
      }
      return false;
    }

    public boolean isResource() {
      return hasSegment("current");
    }
  }
}
