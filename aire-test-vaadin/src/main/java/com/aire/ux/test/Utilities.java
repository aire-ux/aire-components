package com.aire.ux.test;

import static java.lang.String.format;

import com.aire.ux.test.Context.Mode;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.lambda.Option;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.val;

public class Utilities {

  public static final String SPRING_ANNOTATION_NAME = "com.aire.ux.test.spring.EnableSpring";
  static final AtomicBoolean springTypePresent = new AtomicBoolean();

  private Utilities() {}

  public static boolean isSpringTypePresent() {
    if (springTypePresent.get()) {
      return true;
    }
    try {
      Class.forName(SPRING_ANNOTATION_NAME, true, Thread.currentThread().getContextClassLoader());
      springTypePresent.set(true);
    } catch (NoClassDefFoundError | ClassNotFoundException ex) {
      springTypePresent.set(false);
    }
    return springTypePresent.get();
  }

  public static boolean isAnnotationPresent(Class<?> type, Class<?> annotationType) {
    val queue = new ArrayDeque<Class<?>>();
    queue.addAll(
        Arrays.stream(type.getDeclaredAnnotations())
            .map(Annotation::annotationType)
            .collect(Collectors.toList()));

    val seen = new HashSet<>(queue);
    while (!queue.isEmpty()) {
      val annotation = queue.pollFirst();
      if (Objects.equals(annotation, annotationType)) {
        return true;
      }
      for (val ann : annotation.getDeclaredAnnotations()) {
        if (seen.add(ann.annotationType())) {
          queue.add(ann.annotationType());
        }
      }
    }
    return false;
  }

  static boolean isAnnotationPresent(String annotationName, Annotation... types) {

    val queue = new ArrayDeque<Class<?>>();
    queue.addAll(Arrays.stream(types).map(Annotation::annotationType).collect(Collectors.toList()));
    val seen = new HashSet<>(queue);
    while (!queue.isEmpty()) {
      val annotation = queue.pollFirst();
      if (Objects.equals(annotationName, annotation.getName())) {
        return true;
      }
      for (val ann : annotation.getDeclaredAnnotations()) {
        if (seen.add(ann.annotationType())) {
          queue.add(ann.annotationType());
        }
      }
    }
    return false;
  }

  public static boolean isSpringExtensionEnabled(Class<?> testClass) {
    if (isSpringTypePresent()) {
      return Reflect.mapOverHierarchy(
              testClass,
              (t) ->
                  Option.of(
                      isAnnotationPresent(SPRING_ANNOTATION_NAME, t.getDeclaredAnnotations())))
          .anyMatch(t -> t);
    }
    return false;
  }

  /**
   * @param select the annotation to check
   * @return true if the selector is not null and the selector's value or selector expression are
   *     not the default values
   */
  public static boolean isDefault(Select select) {
    return select != null
        && (select.selector().equals(select.value())
            && select.selector().equals(Select.default_value));
  }

  public static boolean isDefault(Navigate test) {
    return test != null
        && Select.default_value.equals(test.to())
        && Select.default_value.equals(test.value());
  }

  public static String firstNonDefault(String... values) {
    for (val value : values) {
      if (!Select.default_value.equals(value)) {
        return value;
      }
    }
    throw new IllegalArgumentException(
        format("No non-default value out of %s", Arrays.asList(values)));
  }

  public static Collection<?> resolveCollection(Class<?> type) {
    if (List.class.isAssignableFrom(type)) {
      return new ArrayList<>();
    }
    if (Set.class.isAssignableFrom(type)) {
      return new LinkedHashSet<>();
    }
    throw new UnsupportedOperationException(format("Can't resolve a collection of type: %s", type));
  }

  @SuppressWarnings("unchecked")
  public static Class<?> resolveCollectionType(Class<?> type) {
    if (List.class.isAssignableFrom(type)) {
      return ArrayList.class;
    }
    if (Set.class.isAssignableFrom(type)) {
      return LinkedHashSet.class;
    }
    throw new UnsupportedOperationException(format("Can't resolve a collection of type: %s", type));
  }

  public static Option<String> notBlank(String s) {
    return (s == null || s.isBlank() ? Option.none() : Option.some(s));
  }

  public static boolean isMode(Context context, Mode mode) {
    if (context == null) {
      return mode == Mode.None;
    }
    return context.mode() == mode;
  }
}
