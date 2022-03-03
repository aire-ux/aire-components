package io.sunshower.zephyr.core.test;

import com.aire.ux.Aire;
import com.aire.ux.Selection;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.Select;
import com.aire.ux.test.Utilities;
import com.vaadin.flow.component.UI;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import lombok.val;

public class PathRegistryParameterResolver implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (!(element instanceof Parameter)) {
      return false;
    }
    val selector = element.getAnnotation(Select.class);
    if (selector == null) {
      return false;
    }
    return !Utilities.isDefault(selector) && "path".equalsIgnoreCase(selector.mode());
  }

  @Override
  public ElementResolver create(AnnotatedElement element) {

    return new ElementResolver() {
      @Override
      @SuppressWarnings("unchecked")
      public <T> T resolve() {
        val selector = element.getAnnotation(Select.class);
        return (T)
            Selection.path(Utilities.firstNonDefault(selector.value(), selector.selector()))
                .select(Aire.getUserInterface(), UI::getCurrent)
                .get();
      }
    };
  }
}
