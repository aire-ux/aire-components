package com.aire.ux.test.spring;

import com.aire.ux.test.vaadin.Frames;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Order(100)
public class AireSpringVaadinExtension extends SpringExtension {

  static final Map<Class<?>, Boolean> cache =
      new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Entry<Class<?>, Boolean> eldest) {
          return size() >= 100;
        }
      };

  public static ApplicationContext getApplicationContext() {
    return SpringExtension.getApplicationContext(Frames.currentContext());
  }

  //  public boolean supportsParameter(
  //      ParameterContext parameterContext, ExtensionContext extensionContext)
  //      throws ParameterResolutionException {
  //
  //    val stack = new ArrayDeque<Class<?>>();
  //    extensionContext.getTestClass().ifPresent(stack::add);
  //    val seen = new HashSet<Class<?>>();
  //    while (!stack.isEmpty()) {
  //      val type = stack.pop();
  //      if (cache.containsKey(type)) {
  //        return false;
  //      }
  //      seen.add(type);
  //      if (hasSpringAnnotation(type)) {
  //        cache.put(type, true);
  //        return false;
  //      }
  //
  //      for (val annotation : type.getAnnotations()) {
  //        val annotationType = annotation.annotationType();
  //        if (!seen.contains(annotationType)) {
  //          stack.push(annotationType);
  //        }
  //      }
  //      val supertype = type.getSuperclass();
  //      if (!(supertype == null || Object.class.equals(supertype) || seen.contains(supertype))) {
  //        stack.push(supertype);
  //      }
  //      for (val iface : type.getInterfaces()) {
  //        if (!seen.contains(iface)) {
  //          stack.push(iface);
  //        }
  //      }
  //    }
  //
  //    return false;
  //  }

  private boolean hasSpringAnnotation(Class<?> c) {
    val annotation = c.getAnnotation(ExtendWith.class);
    return annotation != null
        && Stream.of(annotation.value()).anyMatch(SpringExtension.class::isAssignableFrom);
  }
}
