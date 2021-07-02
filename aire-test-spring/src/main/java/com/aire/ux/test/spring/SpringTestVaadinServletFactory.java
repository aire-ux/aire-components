package com.aire.ux.test.spring;

import com.aire.ux.test.InstantiatorFactory;
import com.aire.ux.test.VaadinServletFactory;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.mock.MockInstantiator;
import com.github.mvysny.kaributesting.v10.mock.MockedUI;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServletService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.spring.SpringInstantiator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import lombok.val;

public class SpringTestVaadinServletFactory implements VaadinServletFactory {

  @Override
  public Supplier<UI> getUIFactory() {
    return () -> {
      try {
        return AireSpringVaadinExtension.getApplicationContext().getBean(UI.class);
      } catch (Exception ex) {
        return new MockedUI();
      }
    };
  }

  @Override
  public Optional<VaadinServlet> createServlet(Routes routes) {
    val context = AireSpringVaadinExtension.getApplicationContext();
    return Optional.of(
        new MockSpringServlet(routes, context, () -> getUIFactory().get()) {

          @Override
          protected VaadinServletService createServletService(
              DeploymentConfiguration deploymentConfiguration) throws ServiceException {
            val service =
                new MockSpringServletService(this, deploymentConfiguration, ctx, uiFactory) {
                  @Override
                  public Instantiator getInstantiator() {
                    val factories =
                        ServiceLoader.load(
                            InstantiatorFactory.class,
                            Thread.currentThread().getContextClassLoader())
                            .iterator();
                    Instantiator current =
                        new MockInstantiator(new SpringInstantiator(this, context));
                    while (factories.hasNext()) {
                      current = factories.next().create(current);
                    }
                    return current;
                  }
                };
            service.init();
            routes.register(service.getContext());
            return service;
          }
        });
  }
}
