package com.aire.ux.condensation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.condensation.mappings.AnnotationDrivenPropertyScanner;
import com.aire.ux.condensation.mappings.CachingDelegatingTypeInstantiator;
import com.aire.ux.condensation.mappings.DefaultTypeBinder;
import com.aire.ux.condensation.mappings.ReflectiveTypeInstantiator;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TypeBinderTest {

  private PropertyScanner scanner;
  private DefaultTypeBinder binder;
  private ReflectiveTypeInstantiator instantiator;

  @BeforeEach
  void setUp() {
    instantiator = new ReflectiveTypeInstantiator();
    scanner =
        new AnnotationDrivenPropertyScanner(new CachingDelegatingTypeInstantiator(instantiator));
    binder = new DefaultTypeBinder(scanner);
  }

  @Test
  void ensureReadingSimpleDocumentWorks() {
    @RootElement
    class A {

      @Attribute
      private String name;
    }

    val document = "{\n" + "  \"name\": \"hello\"\n" + "}";
    instantiator.register(A.class, A::new);
    val result = Condensation.read(A.class, "json", document, binder);
    assertEquals(result.name, "hello");
  }

  @Test
  void ensureReadingSimpleDocumentWorksWithAttributeAliasWorks() {
    @RootElement
    class A {

      @Attribute(alias = @Alias(read = "waddup"))
      private String name;
    }

    val document = "{\n" + "  \"waddup\": \"hello\"\n" + "}";
    instantiator.register(A.class, A::new);
    val result = Condensation.read(A.class, "json", document, binder);
    assertEquals(result.name, "hello");
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "-1", "1e1", "-1e-1", "-1e1"})
  void ensureReadingSimpleDocumentWorksWithInt(String value) {

    @RootElement
    class A {

      @Attribute
      private Integer name;
    }

    val document = String.format("{\n" + "  \"name\": %s\n" + "}", value);
    instantiator.register(A.class, A::new);
    val result = Condensation.read(A.class, "json", document, binder);
    assertEquals(Double.valueOf(value).intValue(), result.name);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "-1", "200", "-134"})
  void ensureReadingSimpleDocumentWorksWithDouble(String value) {

    @RootElement
    class A {

      @Attribute
      private Double name;
    }

    val document = String.format("{\n" + "  \"name\": %s\n" + "}", value);
    instantiator.register(A.class, A::new);
    val result = Condensation.read(A.class, "json", document, binder);
    assertEquals(Integer.parseInt(value), result.name);
  }

  @Test
  void ensureMutatorsWorkWithNoAlias() {

    @RootElement
    class A {

      private String _name;

      @Attribute
      public String getName() {
        return _name;
      }

      public void setName(String name) {
        _name = name;
      }
    }

    val document = String.format("{\n" + "  \"name\": \"josiah\"\n" + "}");
    instantiator.register(A.class, A::new);
    val result = Condensation.read(A.class, "json", document, binder);
    assertEquals("josiah", result.getName());
  }


  @Test
  void ensureMutatorsWorkWithAlias() {

    @RootElement
    class A {

      private String _name;

      @Attribute(alias = @Alias(read = "waddup"))
      public String getName() {
        return _name;
      }

      public void setName(String name) {
        _name = name;
      }
    }

    val document = String.format("{\n" + "  \"waddup\": \"josiah\"\n" + "}");
    instantiator.register(A.class, A::new);
    val result = Condensation.read(A.class, "json", document, binder);
    assertEquals("josiah", result.getName());
  }


  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_doubles_field() {
    @RootElement
    class A {

      @Attribute
      private double[] doubles;
    }

    val document = "\n"
                   + "{\n"
                   + "  \"doubles\": [1,2,3,4,5, 1e-17,1e7, -1E-17]\n"
                   + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new double[]{1, 2, 3, 4, 5, 1e-17, 1e7, -1E-17}, result.doubles);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_float_field() {
    @RootElement
    class A {

      @Attribute
      private float[] floats;
    }

    val document = "\n"
                   + "{\n"
                   + "  \"floats\": [1,2,3,4,5, 1e-3,1e4, -1E-7]\n"
                   + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new float[]{1, 2, 3, 4, 5, (float) 1e-3, (float) 1e4, (float) -1E-7},
        result.floats);
  }

}
