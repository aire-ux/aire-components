package com.aire.ux.condensation;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.condensation.mappings.ReflectiveTypeInstantiator;
import lombok.val;
import org.junit.jupiter.api.Test;

class CondensationTest {

  @Test
  void ensureBinderWorks() {
    @RootElement
    class A {
      @Attribute String name;
    }
    val condensation = Condensation.create("json");
    ((ReflectiveTypeInstantiator) condensation.getInstantiator()).register(A.class, A::new);
    val result = condensation.read(A.class, "{\"name\":\"josiah\"}");
    assertEquals(result.name, "josiah");
  }

  @Test
  void ensureDoubleExampleworks() {

    Condensation condensation = Condensation.create("json");
    double[] values = condensation.read(double[].class, "[1,2,3,4]");
    assertArrayEquals(new double[] {1d, 2d, 3d, 4d}, values);
  }

  @Test
  void ensureIntExampleWorks() {

    Condensation condensation = Condensation.create("json");
    int[] values = condensation.read(int[].class, "[1,2,3,4]");
    assertArrayEquals(new int[] {1, 2, 3, 4}, values);
  }

  @Test
  void ensureObjectExampleWorks() {}
}
