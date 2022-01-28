package io.sunshower.zephyr.ui.rmi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class ParametersTest {

  @Test
  void ensureParameterWorksForNoArguments() {
    assertEquals("this.test()", Parameters.constructExpression("test"));
  }

  @Test
  void ensureParameterWorksForSingleArgument() {
    assertEquals("this.test($0)", Parameters.constructExpression("test", String.class));
  }

  @Test
  void ensureParameterWorksForMultipleArguments() {
    assertEquals("this.test($0,$1)",
        Parameters.constructExpression("test", String.class, Integer.class));
  }
}