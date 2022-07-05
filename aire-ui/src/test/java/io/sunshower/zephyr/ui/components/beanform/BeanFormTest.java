package io.sunshower.zephyr.ui.components.beanform;

import static org.junit.jupiter.api.Assertions.*;

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
  void ensureResponsiveFormsAreScanned() {
    assertEquals(2, formType.getResponsiveSteps().size());
  }

}