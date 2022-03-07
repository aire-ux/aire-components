package io.sunshower.zephyr.aire;

import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.SpringServlet;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.Kernel;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.context.WebApplicationContext;

public class AireVaadinServlet extends SpringServlet implements DisposableBean {

  private final Kernel kernel;
  private final AccessQueue queue;
  private final Module kernelModule;
  private final WebApplicationContext context;
  private Registration uiListenerRegistration;
  private Registration sessionInitRegistration;
  private Registration sessionDestroyRegistration;

  public AireVaadinServlet(Kernel kernel, Module kernelModule, AccessQueue queue,
      WebApplicationContext context,
      boolean rootMapping) {
    super(context, rootMapping);
    this.queue = queue;
    this.kernel = kernel;
    this.context = context;
    this.kernelModule = kernelModule;
  }

  @Override
  protected VaadinServletService createServletService(
      DeploymentConfiguration deploymentConfiguration) throws ServiceException {
    val service = new VaadinSpringServletService(kernel, kernelModule, this,
        deploymentConfiguration, context);
    uiListenerRegistration = service.addUIInitListener(queue);
    sessionInitRegistration = service.addSessionInitListener(queue);
    sessionDestroyRegistration = service.addSessionDestroyListener(queue);
    service.init();
    return service;
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
