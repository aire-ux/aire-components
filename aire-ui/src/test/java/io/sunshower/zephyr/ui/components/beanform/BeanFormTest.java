package io.sunshower.zephyr.ui.components.beanform;

import static io.sunshower.zephyr.ui.aire.DOM.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.sunshower.arcus.reflect.Reflect;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeanFormTest {

  TestFormBean bean;
  BeanForm<TestFormBean> formType;

  @BeforeEach
  void setUp() {
    bean = new TestFormBean();
    formType = new BeanForm<>(TestFormBean.class, bean);
  }

  @Test
  void ensureSelectingWorks() {
    assertFalse($(formType).querySelector("vaadin-text-field").isEmpty());
  }

  @Test
  void ensureResponsiveFormsAreScanned() {
    assertEquals(2, formType.getResponsiveSteps().size());
  }

  @Test
  void ensureBasicFieldIsScanned() {
    assertTrue(formType.getDescriptor("basicField").isPresent());
  }

  @Test
  void ensureColumnValuesAreCopied() {
    assertEquals(
        List.of(1, 2),
        formType.getResponsiveSteps().stream()
            .flatMap(t -> Reflect.fieldValue(t, "columns").stream())
            .collect(Collectors.toList()));
  }

  @Test
  void ensureWidthValuesAreCopied() {
    assertEquals(
        List.of("0", "320px"),
        formType.getResponsiveSteps().stream()
            .flatMap(t -> Reflect.fieldValue(t, "minWidth").stream())
            .collect(Collectors.toList()));
  }
}
