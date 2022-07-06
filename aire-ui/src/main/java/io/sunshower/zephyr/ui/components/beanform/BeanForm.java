package io.sunshower.zephyr.ui.components.beanform;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.data.binder.Binder;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonString;
import io.sunshower.arcus.condensation.Property;
import io.sunshower.arcus.condensation.PropertyScanner;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.gyre.Pair;
import java.beans.BeanDescriptor;
import java.beans.Beans;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.val;

/**
 * a bean form binds a type to a form. When the form is populated, the bean's bound properties will
 * be, too
 *
 * @param <T>
 */
public class BeanForm<T> extends FormLayout {

  static final Map<Class<?>, FieldComponentSupplier<?>> typeRegistry;

  static {
    typeRegistry = new HashMap<>();
    typeRegistry.put(String.class, new StringFieldComponentSupplier());
    typeRegistry.put(Boolean.class, new CheckboxFieldComponentSupplier());
  }

  private final T value;
  private final Class<T> type;
  private final PropertyScanner propertyScanner;
  private final Map<String, SupplierBinder> fieldDescriptors;


  public BeanForm(@NonNull final Class<T> type, @NonNull final T value) {
    this.type = type;
    this.value = value;
    this.fieldDescriptors = new LinkedHashMap<>();
    this.propertyScanner = new BeanFormPropertyScanner();
    scan(type);
  }

  public BeanForm(@NonNull final Class<T> type) {
    this(type, Reflect.instantiate(type));
  }

  public T getValue() {
    return value;
  }

  public Optional<FieldDescriptor> getDescriptor(String name) {
    return Optional.ofNullable(fieldDescriptors.get(name)).map(SupplierBinder::descriptor);
  }

  public boolean hasDescriptor(String name) {
    return fieldDescriptors.containsKey(name);
  }

  public Collection<FieldDescriptor> getFields() {
    return fieldDescriptors.values().stream()
        .map(SupplierBinder::descriptor)
        .collect(Collectors.toUnmodifiableSet());
  }

  public List<ResponsiveStep> getResponsiveSteps() {
    val stepsJsonArray = (JsonArray) getElement().getPropertyRaw("responsiveSteps");
    if (stepsJsonArray == null) {
      return Collections.emptyList();
    }
    List<ResponsiveStep> steps = new ArrayList<>();
    for (int i = 0; i < stepsJsonArray.length(); i++) {
      steps.add(readJson(stepsJsonArray.get(i)));
    }
    return steps;
  }

  private ResponsiveStep readJson(JsonObject value) {
    val minWidth = value.getString("minWidth");
    val columns = (int) value.getNumber("columns");
    var lp = (JsonString) value.get("labelsPosition");

    LabelsPosition position = null;
    if (lp != null) {
      val lps = lp.getString();
      if ("aside".equals(lps)) {
        position = LabelsPosition.ASIDE;
      } else if ("top".equals(lps)) {
        position = LabelsPosition.TOP;
      }
    }
    return new ResponsiveStep(minWidth, columns, position);
  }

  private void scan(Class<T> type) {
    scanResponsiveSteps(type);
    scanFields(type);
  }

  private void scanFields(Class<T> type) {
    val bundle = ResourceBundle.getBundle("i18n." + type.getName());
    val typeDescriptor = propertyScanner.scan(type);
    typeDescriptor.getProperties().forEach(property -> {
      val name = property.getMemberNormalizedName();
      val member = property.getMember();
      val fieldAnnotation = member.getAnnotation(Field.class);
      val keyName = getKeyName(property, fieldAnnotation);
      val label = bundle.getString(keyName);
      val fieldName = property.getMemberNormalizedName();
      val fd = new FieldDescriptor(label, property);

      val supplier = resolveComponentSupplier(property.getType());
      if(supplier == null) {
        add(new BeanForm<>(property.getType()));
      } else {
        val binder = new SupplierBinder(fd, fieldAnnotation,
            resolveComponentSupplier(property.getType()));
        fieldDescriptors.put(fieldName, binder);
        addFormElement(binder);
      }

    });
  }

  private void addFormElement(SupplierBinder binder) {
    val element = binder.create();
    val itemBinder = new Binder<>(type);
    itemBinder.bind(element, binder.descriptor.property()::get, binder.descriptor.property()::set);
    this.add((Component) element);
  }

  private String getKeyName(Property<?> property, Field fieldAnnotation) {
    return "__default__".equals(fieldAnnotation.key()) ?
        property.getMemberNormalizedName() : fieldAnnotation.key();
  }

  private Set<? extends Annotation> collectFieldAnnotations(java.lang.reflect.Field field) {
    return Arrays.stream(field.getAnnotations())
        .filter(annotation -> annotation.annotationType().isAnnotationPresent(FieldMarker.class))
        .collect(Collectors.toUnmodifiableSet());
  }

  private FieldComponentSupplier<?> resolveComponentSupplier(Class<?> type) {
    return typeRegistry.get(type);
  }

  private void scanResponsiveSteps(Class<T> type) {
    setResponsiveSteps(
        Arrays.stream(type.getAnnotationsByType(ResponsiveBreak.class))
            .map(this::scanResponsiveStep)
            .collect(Collectors.toList()));
  }

  private ResponsiveStep scanResponsiveStep(ResponsiveBreak breakpoint) {
    val width = breakpoint.width();
    val columns = breakpoint.columns();
    val position =
        switch (breakpoint.position()) {
          case Top -> LabelsPosition.TOP;
          case Aside -> LabelsPosition.ASIDE;
        };
    return new ResponsiveStep(width, columns);
  }

  private static record SupplierBinder(
      FieldDescriptor descriptor, Field fieldAnnotation, FieldComponentSupplier<?> supplier) {

    public HasValue<?, ?> create() {
      return supplier.create(descriptor, fieldAnnotation);
    }
  }
}
