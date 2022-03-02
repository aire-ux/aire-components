package io.sunshower.zephyr.aire;

import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.spring.SpringServlet;
import lombok.val;
import org.springframework.web.context.WebApplicationContext;

public class AireVaadinServlet extends SpringServlet
    implements SessionInitListener, SessionDestroyListener {

  private final AccessQueue queue;
  private final WebApplicationContext context;

  public AireVaadinServlet(
      AccessQueue queue, WebApplicationContext context, boolean rootMapping) {
    super(context, rootMapping);
    this.queue = queue;
    this.context = context;
  }

  @Override
  protected VaadinServletService createServletService(
      DeploymentConfiguration deploymentConfiguration) throws ServiceException {
    val service = new VaadinSpringServletService(this, deploymentConfiguration, context);
    service.init();
    return service;
  }

  @Override
  public void sessionDestroy(SessionDestroyEvent event) {
  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException {
    queue.drain(event.getSession());
  }
}
