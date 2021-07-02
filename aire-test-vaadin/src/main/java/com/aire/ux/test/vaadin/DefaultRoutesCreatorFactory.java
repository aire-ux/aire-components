package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireExtension;
import com.github.mvysny.kaributesting.v10.Routes;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DefaultRoutesCreatorFactory implements RoutesCreatorFactory {

  @Override
  public boolean appliesTo(ExtensionContext context, AireExtension extension) {
    return true;
  }

  @Override
  public RoutesCreator create(ExtensionContext context, AireExtension extension) {
    return Routes::new;
  }
}
