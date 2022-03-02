package io.sunshower.zephyr.aire;

import com.aire.ux.core.decorators.CompositeComponentDecorator;
import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.aire.ux.ext.ExtensionComponentDecorator;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.spring.SpringInstantiator;
import com.vaadin.flow.spring.SpringVaadinServletService;
import java.util.List;
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
  @SuppressWarnings("PMD")
  protected Optional<Instantiator> loadInstantiators() throws ServiceException {
    val delegate = new SpringInstantiator(this, context);
    val decorator =
        new CompositeComponentDecorator(
            List.of(
                new ExtensionComponentDecorator(delegate.getOrCreate(ExtensionRegistry.class)),
                new ServiceLoaderComponentDecorator(
                    () -> Thread.currentThread().getContextClassLoader())));
    val instantiator = new BaseAireInstantiator(delegate, decorator);
    return Optional.of(instantiator);
  }
}
