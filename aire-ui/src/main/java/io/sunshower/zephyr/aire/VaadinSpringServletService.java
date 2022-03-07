package io.sunshower.zephyr.aire;

import com.aire.ux.core.decorators.CompositeComponentDecorator;
import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;
import com.aire.ux.ext.ExtensionComponentDecorator;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringDelegatingInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.spring.SpringInstantiator;
import com.vaadin.flow.spring.SpringVaadinServletService;
import io.zephyr.api.ServiceRegistration;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.FactoryServiceDefinition;
import io.zephyr.kernel.core.Kernel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.context.WebApplicationContext;

public class VaadinSpringServletService extends SpringVaadinServletService implements
    DisposableBean {


  private final Kernel kernel;
  private final Module kernelModule;
  private final WebApplicationContext context;
  private final List<ServiceRegistration<?>> registrations;

  public VaadinSpringServletService(
      Kernel kernel,
      Module kernelModule,
      AireVaadinServlet servlet,
      DeploymentConfiguration deploymentConfiguration,
      WebApplicationContext context) {
    super(servlet, deploymentConfiguration, context);
    this.kernel = kernel;
    this.context = context;
    this.kernelModule = kernelModule;
    this.registrations = new ArrayList<>();
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
    val instantiator = new SpringDelegatingInstantiator(delegate, decorator, context);
    registrations.add(kernel.getServiceRegistry().register(kernelModule,
        new FactoryServiceDefinition<>(SpringDelegatingInstantiator.class, () -> instantiator)));
    return Optional.of(instantiator);
  }


  @Override
  public void destroy() {
    try {
      val riter = registrations.iterator();
      while (riter.hasNext()) {
        val next = riter.next();
        next.dispose();
        riter.remove();
      }
    } finally {
      super.destroy();
    }
  }
}
