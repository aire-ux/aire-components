package servlet;

import io.zephyr.kernel.Lifecycle.State;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.core.ModuleCoordinate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@WebServlet(urlPatterns = "/kernel", name = "zephyr:kernel:resource:servlet")
public class ZephyrModuleResourceServlet extends HttpServlet {

  private final Kernel kernel;
  private final Module rootModule;

  public ZephyrModuleResourceServlet(final Kernel kernel, final Module rootModule) {
    this.kernel = kernel;
    this.rootModule = rootModule;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    val moduleCoordinate = req.getParameter("module");
    var resourceName = req.getParameter("resource");
    if (resourceName == null) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    if (!resourceName.startsWith("ZEPHYR-INF/resources")) {
      if (resourceName.charAt(0) != '/') {
        resourceName = '/' + resourceName;
      }
      resourceName = "ZEPHYR-INF/resources" + resourceName;
    }
    val module = lookupModule(moduleCoordinate);
    val resource = module.getClassLoader().getResource(resourceName);
    if (resource == null) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    resp.setContentType(resourceName);
    resp.setStatus(HttpServletResponse.SC_OK);
    try {
      val connection = resource.openConnection();
      try (val inputStream = connection.getInputStream()) {
        resp.setContentType(connection.getContentType());
        inputStream.transferTo(resp.getOutputStream());
      }
    } catch (Exception ex) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.setContentType("text/plain");
      resp.getOutputStream().write(ex.getMessage().getBytes(StandardCharsets.UTF_8));
    }
  }

  private Module lookupModule(String moduleCoordinate) {
    try {
      val active = kernel.getModuleManager().getModules(State.Active);
      for (val m : active) {
        if (m.getCoordinate().toCanonicalForm().startsWith(moduleCoordinate)) {
          return m;
        }
      }
      val module = kernel.getModuleManager().getModule(ModuleCoordinate.parse(moduleCoordinate));
      if (module != null) {
        return module;
      }
    } catch (Exception ex) {
      log.info("Failed to locate module: '{}'", moduleCoordinate);
    }
    return rootModule;
  }
}
