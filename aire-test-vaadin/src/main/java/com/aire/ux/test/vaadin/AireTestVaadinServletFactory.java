package com.aire.ux.test.vaadin;

import com.aire.ux.test.InstantiatorFactory;
import com.aire.ux.test.VaadinServletFactory;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.mock.MockService;
import com.github.mvysny.kaributesting.v10.mock.MockVaadinServlet;
import com.github.mvysny.kaributesting.v10.mock.MockedUI;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class AireTestVaadinServletFactory implements VaadinServletFactory {

  @Override
  public Supplier<UI> getUIFactory() {
    return MockedUI::new;
  }

  @Override
  public Optional<VaadinServlet> createServlet(Routes routes) {
    return Optional.of(
        new MockVaadinServlet(routes) {
          @NotNull
          @Override
          protected VaadinServletService createServletService(
              @NotNull DeploymentConfiguration deploymentConfiguration) {
            val result =
                new MockService(this, deploymentConfiguration, this.getUiFactory()) {
                  @NotNull
                  @Override
                  public Instantiator getInstantiator() {
                    var instantiator = super.getInstantiator();
                    for (val instantiatorFactory :
                        ServiceLoader.load(
                            InstantiatorFactory.class,
                            Thread.currentThread().getContextClassLoader())) {
                      instantiator = instantiatorFactory.create(instantiator);
                    }
                    return instantiator;
                  }
                };
            try {
              result.init();
              routes.register(result.getContext());
            } catch (Exception ex) {
              throw new IllegalStateException(ex);
            }
            return result;
          }
        });
  }
}
