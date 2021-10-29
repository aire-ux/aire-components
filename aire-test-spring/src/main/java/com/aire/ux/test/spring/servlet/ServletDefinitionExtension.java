package com.aire.ux.test.spring.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.Servlet;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ServletDefinitionExtension implements Extension, BeforeAllCallback, AfterAllCallback {

  private static final String KEY = "DEFINITION_STORE";

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    val applicationContext = SpringExtension.getApplicationContext(context);
    unregisterServletDefinitions(applicationContext, context);
  }


  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    val applicationContext = SpringExtension.getApplicationContext(context);
    registerServletDefinitions(applicationContext, context);
  }

  private void registerServletDefinitions(ApplicationContext applicationContext,
      ExtensionContext context) {

    val store = context.getStore(Namespace.create(applicationContext, context));

    context
        .getTestClass()
        .flatMap(testClass -> Optional.ofNullable(testClass.getAnnotation(WithServlets.class)))
        .ifPresent(withServlets -> {
          defineServlets(store, withServlets,
              (ConfigurableListableBeanFactory)
                  applicationContext.getAutowireCapableBeanFactory());
        });

    postProcessBeanFactory(store,
        ((ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory()));
  }

  private void postProcessBeanFactory(Store store, ConfigurableListableBeanFactory beanFactory)
      throws BeansException {

    if (!(beanFactory instanceof BeanDefinitionRegistry)) {
      throw new IllegalStateException(
          String.format("Wrong type of bean factory (unsupported context): %s",
              beanFactory.getClass()));
    }
    val names = beanFactory.getBeanNamesForAnnotation(WithServlets.class);
    for (val name : names) {
      scanBeanType(store, beanFactory.getBeanDefinition(name), beanFactory);
    }
  }

  private void scanBeanType(Store store, BeanDefinition beanDefinition,
      ConfigurableListableBeanFactory beanFactory) {

    try {
      val classloader = beanFactory.getBeanClassLoader();
      val actualType = Class.forName(beanDefinition.getBeanClassName(), false, classloader);
      val withServletsAnnotation = actualType.getAnnotation(WithServlets.class);
      defineServlets(store, withServletsAnnotation, beanFactory);
    } catch (ClassNotFoundException e) {
      throw new BeanInitializationException(e.getMessage(), e);
    }
  }

  private void defineServlets(Store store, WithServlets withServletsAnnotation,
      ConfigurableListableBeanFactory beanFactory) {
    for (val servlet : withServletsAnnotation.value()) {
      defineServlet(store, servlet, beanFactory);
    }

  }

  @SuppressWarnings("unchecked")
  private void defineServlet(Store store, Class<? extends Servlet> servlet,
      ConfigurableListableBeanFactory beanFactory) {
    val definitions = (List<BeanDefinition>) store.getOrComputeIfAbsent(KEY,
        (k) -> new ArrayList<BeanDefinition>());
    /**
     * register servlet class
     */
    val definition = BeanDefinitionBuilder
        .rootBeanDefinition(servlet).getBeanDefinition();
    ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
        Objects.requireNonNull(definition.getBeanClassName()), definition);

    val servletRegistrationDefinition = BeanDefinitionBuilder.rootBeanDefinition(
            ServletRegistrationBean.class).addConstructorArgReference(definition.getBeanClassName())
        .getBeanDefinition();

    ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
        Objects.requireNonNull(servletRegistrationDefinition.getBeanClassName()),
        servletRegistrationDefinition);
    definitions.addAll(List.of(definition, servletRegistrationDefinition));
  }


  @SuppressWarnings("unchecked")
  private void unregisterServletDefinitions(ApplicationContext applicationContext,
      ExtensionContext context) {

    val store = context.getStore(Namespace.create(applicationContext, context));
    val definitions = (List<BeanDefinition>) store.getOrComputeIfAbsent(KEY,
        (k) -> new ArrayList<BeanDefinition>());

    val registry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    for (val definition : definitions) {
      registry.removeBeanDefinition(
          Objects.requireNonNull(definition.getBeanClassName()));
    }
  }
}
