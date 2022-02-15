package io.sunshower.zephyr.aire;

import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.spring.SpringInstantiator;
import com.vaadin.flow.spring.SpringVaadinServletService;
import java.util.Optional;
import lombok.val;
import org.springframework.web.context.WebApplicationContext;

public class VaadinSpringServletService extends SpringVaadinServletService {

  private final WebApplicationContext context;

  public VaadinSpringServletService(
      AireVaadinServlet servlet,
      DeploymentConfiguration deploymentConfiguration,
      WebApplicationContext context) {
    super(servlet, deploymentConfiguration, context);
    this.context = context;
  }

  @Override
  protected Optional<Instantiator> loadInstantiators() throws ServiceException {
    val delegate = new SpringInstantiator(this, context);
    val instantiator =
        new BaseAireInstantiator(
            delegate,
            new ServiceLoaderComponentDecorator(
                () -> Thread.currentThread().getContextClassLoader()));
    return Optional.of(instantiator);
  }
}
