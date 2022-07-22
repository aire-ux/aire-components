package com.aire.ux.ext.spring;

import com.aire.ux.Registration;
import com.aire.ux.core.decorators.ComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.NavigationEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

@Log
@SuppressWarnings("PMD.UseProperClassLoader")
public class SpringDelegatingInstantiator extends BaseAireInstantiator {

  private final Object lock = new Object();
  private final ApplicationContext rootApplicationContext;

  private final Map<String, ApplicationContext> contexts;

  public SpringDelegatingInstantiator(
      @Nonnull Instantiator delegate,
      @Nonnull ComponentDecorator decorator,
      @Nonnull ApplicationContext rootApplicationContext) {

    super(delegate, decorator);
    this.contexts = new HashMap<>();
    this.rootApplicationContext = rootApplicationContext;
  }

  public Registration performWithParent(
      Function<ApplicationContext, ApplicationContext> supplierConsumer) {
    synchronized (lock) {
      val ctx = supplierConsumer.apply(rootApplicationContext);
      val classloader = ctx.getClassLoader();
      assert classloader != null;
      val name = keyFor(classloader);
      contexts.put(name, ctx);
      return () -> contexts.remove(name);
    }
  }

  protected <T extends HasElement> T doCreateRouteTarget(
      Class<T> routeTargetType, NavigationEvent event) {
    return doGetOrCreate(routeTargetType);
  }

  @Override
  protected <T> T doGetOrCreate(Class<T> type) {

    val result = loadFromRoot(type);
    if (result != null) {
      return result;
    }

    synchronized (contexts) {
      val ctx = contexts.get(keyFor(type.getClassLoader()));
      if (ctx != null) {
        val names = ctx.getBeanNamesForType(type);
        if (names.length > 0) {
          return ctx.getBean(type);
        }
      }
      for (val context : contexts.values()) {
        val names = context.getBeanNamesForType(type);
        if (names.length == 1) {
          return context.getBean(type);
        } else {
          try {
            return context.getAutowireCapableBeanFactory().createBean(type);
          } catch (BeansException ex) {
            log.log(
                Level.INFO,
                "Failed to resolve bean of type ''{0}'' from context: ''{1}''",
                new Object[] {type, ctx.getClassLoader()});
          }
        }
      }
      return super.doGetOrCreate(type);
    }
  }

  private <T> T loadFromRoot(Class<T> type) {

    if (!Objects.equals(type.getClassLoader(), rootApplicationContext.getClassLoader())) {
      return null;
    }
    val names = rootApplicationContext.getBeanNamesForType(type);
    if (names.length > 0) {
      return rootApplicationContext.getBean(names[0], type);
    }
    try {
      return rootApplicationContext.getAutowireCapableBeanFactory().createBean(type);
    } catch (BeansException ex) {
      log.log(
          Level.WARNING,
          "Failed to instantiate bean  of type {0}.  Reason: {1}",
          new Object[] {type, ex.getMessage()});
    }
    return null;
  }

  @Override
  protected <T extends Component> T doCreateComponent(Class<T> type) {
    return doGetOrCreate(type);
  }

  private String keyFor(ClassLoader classLoader) {
    return classLoader.getName();
  }
}
