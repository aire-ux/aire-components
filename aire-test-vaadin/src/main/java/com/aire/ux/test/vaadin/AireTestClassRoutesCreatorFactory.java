package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireExtension;
import com.aire.ux.test.RouteLocation;
import com.aire.ux.test.Select;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.logging.Level;
import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.extension.ExtensionContext;

@Log
public class AireTestClassRoutesCreatorFactory implements RoutesCreatorFactory {

  @Override
  public boolean appliesTo(ExtensionContext context, AireExtension extension) {
    val opt = context.getElement();
    if (opt.isEmpty()) {
      log.log(
          Level.INFO, "%s does not apply to class %s: %s.  Reason: no annotated element present");
      return false;
    }
    val element = opt.get();

    return element.getAnnotationsByType(RouteLocation.class).length > 0
        || context
            .getTestClass()
            .flatMap(cl -> Optional.of(cl.getAnnotationsByType(RouteLocation.class).length > 0))
            .isPresent();
  }

  @Override
  public RoutesCreator create(ExtensionContext context, AireExtension extension) {
    return new AireClassRoutesCreator(context, extension);
  }

  private static class AireClassRoutesCreator implements RoutesCreator {

    private final AireExtension extension;
    private final ExtensionContext context;

    public AireClassRoutesCreator(ExtensionContext context, AireExtension extension) {
      this.context = context;
      this.extension = extension;
    }

    @Override
    public com.github.mvysny.kaributesting.v10.Routes create() {
      val elementOpt =
          context
              .getTestMethod()
              .flatMap(
                  t ->
                      Optional.ofNullable(
                          (AnnotatedElement)
                              (t.isAnnotationPresent(RouteLocation.class) ? t : null)))
              .or(context::getTestClass);
      val routes = new com.github.mvysny.kaributesting.v10.Routes();
      if (elementOpt.isPresent()) {

        val routeAnnotations = elementOpt.get().getAnnotationsByType(RouteLocation.class);
        for (val route : routeAnnotations) {
          val result = getRoutePackage(route);
          if (result != null) {
            routes.autoDiscoverViews(result);
          }
        }
      } else {
        routes.autoDiscoverViews();
      }
      return routes;
    }

    private String getRoutePackage(RouteLocation routeLocation) {
      if (routeLocation == null) {
        return null;
      }
      if (!Void.class.equals(routeLocation.scanClassPackage())) {
        return routeLocation.scanClassPackage().getPackageName();
      }
      if (!routeLocation.scanPackage().equals(Select.default_value)) {
        return routeLocation.scanPackage();
      }
      return null;
    }
  }
}
