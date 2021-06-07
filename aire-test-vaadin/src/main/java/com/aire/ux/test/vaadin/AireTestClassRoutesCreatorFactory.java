package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireExtension;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.Utilities;
import io.sunshower.lambda.Option;
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
    return element.isAnnotationPresent(Routes.class);
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
      val elementOpt = context.getElement();
      val routes = new com.github.mvysny.kaributesting.v10.Routes();
      if(elementOpt.isPresent()) {
        val result = getRoutePackage(elementOpt.get().getAnnotation(Routes.class));
        if(result != null) {
          routes.autoDiscoverViews(result);
        }
      } else {
        routes.autoDiscoverViews();
      }
      return routes;
    }


    private String getRoutePackage(Routes routes) {
      if(!Void.class.equals(routes.scanClassPackage())) {
        return routes.scanClassPackage().getPackageName();
      }
      if(!routes.scanPackage().equals(Select.default_value)) {
        return routes.scanPackage();
      }
      return null;
    }
  }
}
