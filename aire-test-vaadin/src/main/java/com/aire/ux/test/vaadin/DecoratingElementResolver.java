package com.aire.ux.test.vaadin;

import com.aire.ux.test.Context;
import com.aire.ux.test.Context.Mode;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ParameterDecorator;
import java.lang.reflect.AnnotatedElement;
import java.util.ServiceLoader;
import lombok.val;

class DecoratingElementResolver implements ElementResolver {

  private Object value;
  private final Mode mode;
  private final Context context;
  private final TestFrame frame;
  private final ElementResolver delegate;

  public DecoratingElementResolver(
      TestFrame frame,
      ElementResolver resolver,
      AnnotatedElement element
  ) {
    this.frame = frame;
    this.context = resolveContext(element);
    this.mode = resolveMode();
    this.delegate = resolver;
  }


  @Override
  public Mode getMode() {
    return mode;
  }

  @Override
  public <T> T resolve() {
    return decorate(delegate.resolve());
  }

  @SuppressWarnings("unchecked")
  private <T> T decorate(Object resolve) {
    val decorators = ServiceLoader.load(ParameterDecorator.class, Thread.currentThread()
        .getContextClassLoader());
    Object value = resolve;
    for (val decorator : decorators) {
      value = decorator.decorate(value, context, frame);
    }
    return (T) (this.value = value);
  }

  void activate() {
    val decorators = ServiceLoader.load(ParameterDecorator.class, Thread.currentThread()
        .getContextClassLoader());
    for(val decorator : decorators) {
      decorator.activate(value, context, frame);
    }
  }


  void deactivate() {
    val decorators = ServiceLoader.load(ParameterDecorator.class, Thread.currentThread()
        .getContextClassLoader());
    for(val decorator : decorators) {
      decorator.deactivate(value, context, frame);
    }
  }

  private Context resolveContext(AnnotatedElement element) {
    if (element.isAnnotationPresent(Context.class)) {
      return element.getAnnotation(Context.class);
    }
    return null;
  }

  private Mode resolveMode() {
    if(context == null) {
      return Mode.None;
    }
    return context.mode();
  }
}
