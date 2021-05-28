package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireExtension;
import com.aire.ux.test.Routes;
import java.util.logging.Level;
import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.extension.ExtensionContext;

@Log
public class AireTestClassRoutesCreatorFactory implements RoutesCreatorFactory {

  @Override
  public boolean appliesTo(ExtensionContext context, AireExtension extension) {
    val opt = context.getElement();
    if (!opt.isPresent()) {
      log.log(Level.INFO,
          "%s does not apply to class %s: %s.  Reason: no annotated element present");
      return false;
    }
    val element = opt.get();
    if (element instanceof Class) {
      return element.isAnnotationPresent(Routes.class);
    }
    return false;
  }

  @Override
  public RoutesCreator create(ExtensionContext context, AireExtension extension) {
    return new AireClassRoutesCreator(context, extension);
  }

  private static class AireClassRoutesCreator implements RoutesCreator {

    private final AireExtension extension;
    private final ExtensionContext context;

    public AireClassRoutesCreator(
        ExtensionContext context, AireExtension extension) {
      this.context = context;
      this.extension = extension;
    }

    @Override
    public com.github.mvysny.kaributesting.v10.Routes create() {
      val testClass = context.getRequiredTestClass();
      val annotation = testClass.getAnnotation(Routes.class);
      val routes = new com.github.mvysny.kaributesting.v10.Routes();
      if (!annotation.scanPackage().isBlank()) {
        return routes.autoDiscoverViews(annotation.scanPackage());
      } else {
        return routes.autoDiscoverViews();
      }
    }
  }
}
