package io.sunshower.zephyr.aire;

import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.UIInitListener;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.SpringServlet;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.context.WebApplicationContext;

public class AireVaadinServlet extends SpringServlet implements DisposableBean {

  private final AccessQueue queue;
  private final WebApplicationContext context;
  private Registration uiListenerRegistration;
  private Registration sessionInitRegistration;
  private Registration sessionDestroyRegistration;

  public AireVaadinServlet(AccessQueue queue, WebApplicationContext context, boolean rootMapping) {
    super(context, rootMapping);
    this.queue = queue;
    this.context = context;
  }

  @Override
  protected VaadinServletService createServletService(
      DeploymentConfiguration deploymentConfiguration) throws ServiceException {
    val service = new VaadinSpringServletService(this, deploymentConfiguration, context);
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
