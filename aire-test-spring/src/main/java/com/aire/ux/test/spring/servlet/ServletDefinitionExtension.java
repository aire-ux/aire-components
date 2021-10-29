package com.aire.ux.test.spring.servlet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
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
    registerClient(store, applicationContext);
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

  @SuppressWarnings("unchecked")
  private void registerClient(Store store, ApplicationContext applicationContext) {

    val beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    val beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(DefaultClient.class)
        .addConstructorArgValue(applicationContext)
        .getBeanDefinition();
    val definitions = (List<BeanDefinition>) store.getOrComputeIfAbsent(KEY,
        (k) -> new ArrayList<BeanDefinition>());

    definitions.add(beanDefinition);
    beanDefinitionRegistry.registerBeanDefinition(
        Objects.requireNonNull(beanDefinition.getBeanClassName()),
        beanDefinition);
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

    for (val servletDefinition : withServletsAnnotation.servlets()) {
      if (!Servlet.class.equals(servletDefinition.type())) {
        defineServlet(store, servletDefinition.type(), beanFactory, servletDefinition.paths());
      }
    }
    for (val servlet : withServletsAnnotation.value()) {
      if (!Servlet.class.equals(servlet)) {
        defineServlet(store, servlet, beanFactory, getRequestMappings(servlet));
      }
    }

  }

  @SuppressWarnings("unchecked")
  private void defineServlet(Store store, Class<? extends Servlet> servlet,
      ConfigurableListableBeanFactory beanFactory, String[] names) {
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
            ServletRegistrationBean.class)
        .addConstructorArgReference(definition.getBeanClassName())
        .addConstructorArgValue(true)
        .addConstructorArgValue(names)
        .addPropertyValue("initOnStartup", 1)
        .setLazyInit(false)
        .getBeanDefinition();

    ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
        Objects.requireNonNull(definition.getBeanClassName() + "registration"),
        servletRegistrationDefinition);
    definitions.addAll(List.of(definition, servletRegistrationDefinition));
  }

  private String[] getRequestMappings(Class<? extends Servlet> servlet) {
    if (servlet.isAnnotationPresent(WebServlet.class)) {
      return servlet.getAnnotation(WebServlet.class).value();
    }
    throw new UnsupportedOperationException(
        String.format(
            "Error: must annotate '%s' with an @WebServlet containing request mappings",
            servlet));
  }


  @SuppressWarnings("unchecked")
  private void unregisterServletDefinitions(ApplicationContext applicationContext,
      ExtensionContext context) {

    val store = context.getStore(Namespace.create(applicationContext, context));
    val definitions = new HashSet<>((List<BeanDefinition>) store.getOrComputeIfAbsent(KEY,
        (k) -> new ArrayList<BeanDefinition>()));

    val registry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    for (val name : registry.getBeanDefinitionNames()) {
      val definition = registry.getBeanDefinition(name);
      if (definitions.contains(definition)) {
        registry.removeBeanDefinition(name);
      }
    }
  }
}
