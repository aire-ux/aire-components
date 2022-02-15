package io.sunshower.zephyr.spring;

import java.lang.reflect.Constructor;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

public class DynamicConstructorArgumentInstantiationAwareBeanPostProcessor implements
    SmartInstantiationAwareBeanPostProcessor {

  @Override
  public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
      throws BeansException {
    val ctors = beanClass.getDeclaredConstructors();
    return null;

  }
}
