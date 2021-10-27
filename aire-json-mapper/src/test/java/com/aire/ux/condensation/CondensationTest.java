package com.aire.ux.condensation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.condensation.mappings.ReflectiveTypeInstantiator;
import java.util.Map;
import java.util.function.Function;
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
  void ensureMappedPrimitiveExampleWorks() {
    @RootElement
    class KV {

      @Element Map<String, Integer> elements;
    }

    val value = "{" + "\"elements\": {" + "\"1\": 1," + "\"2\": 3}" + "} ";

    val condensation = Condensation.create("json");
    ((ReflectiveTypeInstantiator) condensation.getInstantiator()).register(KV.class, KV::new);
    val result = condensation.read(KV.class, value);
    assertEquals(result.elements.size(), 2);
    assertEquals(result.elements.get("2"), 3);
  }

  @Test
  void ensureKeyConverterWorks() {
    class StringToIntegerConverter implements Function<String, Integer> {

      @Override
      public Integer apply(String s) {
        return Integer.parseInt(s);
      }
    }
    @RootElement
    class KV {

      @Element
      @Convert(key = StringToIntegerConverter.class)
      Map<Integer, Integer> elements;
    }

    val value = "{" + "\"elements\": {" + "\"1\": 1," + "\"2\": 3}" + "} ";

    val condensation = Condensation.create("json");
    ((ReflectiveTypeInstantiator) condensation.getInstantiator()).register(KV.class, KV::new);
    ((ReflectiveTypeInstantiator) condensation.getInstantiator())
        .register(StringToIntegerConverter.class, StringToIntegerConverter::new);
    val result = condensation.read(KV.class, value);
    assertEquals(result.elements.size(), 2);
    assertEquals(result.elements.get(2), 3);
  }
}
