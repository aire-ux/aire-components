package com.aire.ux.ext.spring;

import com.aire.ux.Registration;
import com.aire.ux.core.decorators.ComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.NavigationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

@Log
public class SpringDelegatingInstantiator extends BaseAireInstantiator {

  private final Object lock = new Object();
  private final List<ApplicationContext> contexts;
  private final ApplicationContext rootApplicationContext;

  public SpringDelegatingInstantiator(
      @Nonnull Instantiator delegate,
      @Nonnull ComponentDecorator decorator,
      @Nonnull ApplicationContext rootApplicationContext

  ) {
    super(delegate, decorator);
    this.contexts = new ArrayList<>();
    this.rootApplicationContext = rootApplicationContext;
  }


  public Registration performWithParent(
      Function<ApplicationContext, ApplicationContext> supplierConsumer) {
    synchronized (lock) {
      val ctx = supplierConsumer.apply(rootApplicationContext);
      contexts.add(ctx);
      return () -> contexts.remove(ctx);
    }
  }

  protected <T extends HasElement> T doCreateRouteTarget(Class<T> routeTargetType,
      NavigationEvent event) {
    return doGetOrCreate(routeTargetType);
  }

  @Override
  protected <T> T doGetOrCreate(Class<T> type) {
    synchronized (contexts) {
      for (val ctx : contexts) {
        val names = ctx.getBeanNamesForType(type);
        if (names.length == 1) {
          return ctx.getBean(type);
        } else {
          try {
            return ctx.getAutowireCapableBeanFactory().createBean(type);
          } catch (BeansException ex) {
            log.log(Level.INFO, "Failed to resolve bean of type ''{0}'' from context: ''{1}''",
                new Object[]{type, ctx.getClassLoader()});
          }
        }
      }
      return super.doGetOrCreate(type);

    }
  }

  @Override
  protected <T extends Component> T doCreateComponent(Class<T> type) {
    synchronized (contexts) {
      for (val ctx : contexts) {
        val names = ctx.getBeanNamesForType(type);
        if (names.length == 1) {
          return ctx.getBean(type);
        } else if (names.length > 1) {
          return ctx.getAutowireCapableBeanFactory().createBean(type);
        }
      }
      return super.doCreateComponent(type);
    }
  }
}
