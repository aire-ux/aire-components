package io.sunshower.zephyr.ui.i18n;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValue;
import io.sunshower.zephyr.security.model.User;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;
import lombok.NonNull;
import lombok.val;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

public class InternationalizationBeanPostProcessor implements BeanPostProcessor,
    FieldFilter {

  private final ApplicationContext context;
  private final ResourceBundleResolver resourceBundleResolver;


  public InternationalizationBeanPostProcessor(@NonNull ResourceBundleResolver resolver, @NonNull
      ApplicationContext context) {
    this.context = context;
    this.resourceBundleResolver = resolver;
  }


  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (isLocalizable(bean)) {
      val type = AopUtils.getTargetClass(bean);
      val annotation = type.getAnnotation(Localize.class);
      assert annotation != null;
      doLocalize(type, bean, beanName, annotation);
    }
    return bean;
  }

  private boolean isLocalizable(Object bean) {
    if (bean == null) {
      return false;
    }
    val actualType = AopUtils.getTargetClass(bean);
    return actualType.isAnnotationPresent(Localize.class);
  }

  @SuppressWarnings("unchecked")
  private void doLocalize(Class<?> type, Object bean, String beanName, Localize annotation) {
    val locale = resolveCurrentLocale();
    val resourcebundle = lookup(type, annotation, locale);
    ReflectionUtils.doWithFields(type, field -> {
      val localized = field.getAnnotation(Localized.class);
      assert localized != null;
      val fieldValue = field.get(bean);
      val bundleName =
          "..default..".equals(localized.value()) ? field.getName() : localized.value();
      if (fieldValue instanceof HasText) {
        doLocalize((HasText) fieldValue, localized, resourcebundle, bundleName);
      } else if (fieldValue instanceof HasValue<?, ?>) {
        ((HasValue) fieldValue).setValue(resourcebundle.getString(bundleName));
      } else if (CharSequence.class.isAssignableFrom(field.getType())) {
        field.set(bean, resourcebundle.getString(bundleName));
      } else {
        throw new IllegalArgumentException(String.format(
            "Error: cannot set field %s of type %s on class %s to value %s of type %s", field,
            field.getType(), type, fieldValue, field.getType()));
      }
    }, this);
  }

  private void doLocalize(HasText field, Localized annotation, ResourceBundle resourceBundle,
      String bundleName) {
    field.setText(resourceBundle.getString(bundleName));
  }


  private ResourceBundle lookup(Class<?> type, Localize annotation, Locale locale) {
    val v = resolveBaseName(type, annotation);
    return resourceBundleResolver.resolve(v, locale);
  }

  private String resolveBaseName(Class<?> type, Localize annotation) {
    val annotatedBaseName = annotation.baseName();
    return !"..default..".equals(annotatedBaseName) ? annotatedBaseName : type.getName();
  }


  private Locale resolveCurrentLocale() {
    val authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (!(authentication instanceof User)) {

    }
    return Locale.getDefault();
  }


  @Override
  public boolean matches(Field field) {
    return field.isAnnotationPresent(Localized.class)
           && field.trySetAccessible();
  }
}
