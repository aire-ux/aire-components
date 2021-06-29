package com.aire.ux.theme.servlet.servlet4;

import com.aire.ux.Theme;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.val;

@WebServlet(urlPatterns = "/aire/theme/theme-manager")
public class AireThemeResourceServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/javascript");
    writeScript(response);
  }

  private void writeScript(HttpServletResponse response) throws IOException {
    try (val stream = Theme.class.getResourceAsStream("/META-INF/aire-scripts/index.js")) {
      Objects.requireNonNull(stream).transferTo(response.getOutputStream());
    }
  }
}
