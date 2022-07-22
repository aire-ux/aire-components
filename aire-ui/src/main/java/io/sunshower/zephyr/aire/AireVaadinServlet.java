package io.sunshower.zephyr.aire;

import com.aire.ux.UserInterface;
import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.SpringServlet;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.Kernel;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.context.WebApplicationContext;

public class AireVaadinServlet extends SpringServlet implements DisposableBean {

  private final Kernel kernel;
  private final AccessQueue queue;
  private final Module kernelModule;
  private final WebApplicationContext context;
  private final UserInterface userInterface;
  private Registration uiListenerRegistration;
  private Registration sessionInitRegistration;
  private Registration sessionDestroyRegistration;

  public AireVaadinServlet(
      Kernel kernel,
      Module kernelModule,
      AccessQueue queue,
      UserInterface userInterface,
      WebApplicationContext context,
      boolean rootMapping) {
    super(context, rootMapping);
    this.queue = queue;
    this.kernel = kernel;
    this.context = context;
    this.kernelModule = kernelModule;
    this.userInterface = userInterface;
  }

  @Override
  protected VaadinServletService createServletService(
      DeploymentConfiguration deploymentConfiguration) throws ServiceException {
    val service =
        new VaadinSpringServletService(
            kernel, kernelModule, this, deploymentConfiguration, context);
    uiListenerRegistration = service.addUIInitListener(queue);
    sessionInitRegistration = service.addSessionInitListener(queue);
    sessionDestroyRegistration = service.addSessionDestroyListener(queue);
    service.init();
    return service;
  }

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
    val service = getService();
    service.addSessionDestroyListener(userInterface);
    service.addSessionInitListener(userInterface);
  }

  @Override
  public void destroy() {
    try {
      super.destroy();
    } finally {
      uiListenerRegistration.remove();
      sessionInitRegistration.remove();
      sessionDestroyRegistration.remove();
    }
  }
}
