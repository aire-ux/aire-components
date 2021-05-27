package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireExtension;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface RoutesCreatorFactory {

  boolean appliesTo(ExtensionContext context, AireExtension extension);

  RoutesCreator create(ExtensionContext context, AireExtension extension);
}
