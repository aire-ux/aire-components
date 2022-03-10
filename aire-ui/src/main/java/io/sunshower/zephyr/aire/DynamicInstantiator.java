package io.sunshower.zephyr.aire;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import io.sunshower.zephyr.spring.Dynamic;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Optional;
import lombok.NonNull;
import lombok.val;
import org.springframework.aop.support.AopUtils;

public class DynamicInstantiator {

  @SuppressWarnings("unchecked")
  public static <T> Optional<T> create(UI ui, Class<T> type, Object... args) {
    val instantiator = Instantiator.get(ui);
    if (args.length == 0) {
      return Optional.of(instantiator.getOrCreate(type));
    }
    val ctorDefinition = resolveConstructor(type);
    return ctorDefinition
        .flatMap(
            def -> {
              val parameters = def.getParameters();
              val actualArgs = new Object[parameters.length];
              for (int i = 0; i < parameters.length; i++) {
                val parameter = parameters[i];
                if (parameter.isAnnotationPresent(Dynamic.class)) {
                  actualArgs[i] = resolveDynamicArgument(parameter, args);
                } else {
                  actualArgs[i] = instantiator.getOrCreate(parameter.getType());
                }
              }
              try {
                return Optional.of(def.newInstance(actualArgs));
              } catch (InstantiationException
                  | IllegalAccessException
                  | InvocationTargetException e) {
                return Optional.empty();
              }
            })
        .or(
            () -> {
              if (Component.class.isAssignableFrom(type)) {
                return Optional.of(
                    (T) instantiator.createComponent((Class<? extends Component>) type));
              }
              return Optional.empty();
            });
  }

  private static Object resolveDynamicArgument(Parameter parameter, Object[] args) {
    for (Object arg : args) {
      if (arg == null) {
        return null;
      }
      if (parameter.getType().isAssignableFrom(AopUtils.getTargetClass(arg))) {
        return arg;
      }
    }
    return null;
  }

  @NonNull
  @SuppressWarnings("unchecked")
  private static <T> Optional<Constructor<T>> resolveConstructor(Class<T> type) {
    for (val constructor : type.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(Dynamic.class)) {
        return Optional.of((Constructor<T>) constructor);
      }
    }
    return Optional.empty();
  }
}
