package com.aire.ux.condensation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

      @Attribute private String name;
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

      @Attribute private Integer name;
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

      @Attribute private Double name;
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
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_Doubles_field() {
    @RootElement
    class A {

      @Attribute private Double[] doubles;
    }

    val document = "\n" + "{\n" + "  \"doubles\": [1,2,3,4,5, 1e-17,1e7, -1E-17]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new Double[] {1d, 2d, 3d, 4d, 5d, 1e-17, 1e7, -1E-17}, result.doubles);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_doubles_field() {
    @RootElement
    class A {

      @Attribute private double[] doubles;
    }

    val document = "\n" + "{\n" + "  \"doubles\": [1,2,3,4,5, 1e-17,1e7, -1E-17]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new double[] {1, 2, 3, 4, 5, 1e-17, 1e7, -1E-17}, result.doubles);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_float_field() {
    @RootElement
    class A {

      @Attribute private float[] floats;
    }

    val document = "\n" + "{\n" + "  \"floats\": [1,2,3,4,5, 1e-3,1e4, -1E-7]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(
        new float[] {1, 2, 3, 4, 5, (float) 1e-3, (float) 1e4, (float) -1E-7}, result.floats);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_Float_field() {
    @RootElement
    class A {

      @Attribute private Float[] floats;
    }

    val document = "\n" + "{\n" + "  \"floats\": [1,2,3,4,5, 1e-3,1e4, -1E-7]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new Float[] {1f, 2f, 3f, 4f, 5f, 1e-3f, 1e4f, -1E-7f}, result.floats);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_Boolean_field() {
    @RootElement
    class A {

      @Attribute private Boolean[] booleans;
    }

    val document = "\n" + "{\n" + "  \"booleans\": [true, false, true,   true]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new Boolean[] {true, false, true, true}, result.booleans);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_boolean_field() {
    @RootElement
    class A {

      @Attribute private boolean[] booleans;
    }

    val document = "\n" + "{\n" + "  \"booleans\": [true, false, true,   true]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new boolean[] {true, false, true, true}, result.booleans);
  }

  @Test
  void ensureReadingStringArrayWorks() {
    val document = "{\n" + "  \"strings\": [\"one\", \"two\", \"three!\"]\n" + "}";

    @RootElement
    class A {

      @Attribute private String[] strings;
    }

    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new String[] {"one", "two", "three!"}, result.strings);
  }

  @Test
  void ensureReadingNestedObjectWorks() {
    @RootElement
    class B {

      @Attribute String hello;
    }
    @RootElement
    class A {

      @Element private B b;
    }

    val document = "{\n" + "  \"b\": {\n" + "    \"hello\": \"world\"\n" + "  }\n" + "}";

    instantiator.register(A.class, A::new);
    instantiator.register(B.class, B::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertNotNull(result.b);
    assertEquals(result.b.hello, "world");
  }

  @Test
  void ensureReadingDeeplyNestedObjectWorks() {

    @RootElement
    class D {

      @Element int[] values;
    }
    @RootElement
    class C {

      @Element D d;

      @Element String name;
    }
    @RootElement
    class B {

      @Attribute String hello;

      @Element private C c;
      @Element private D d;
    }
    @RootElement
    class A {

      @Attribute String name;

      @Element private B b;
    }

    val document =
        "{\n"
            + "  \"name\": \"josiah\",\n"
            + "  \"b\": {\n"
            + "    \"hello\": \"world\",\n"
            + "    \"d\": {\n"
            + "      \"values\": [\n"
            + "        1,\n"
            + "        2,\n"
            + "        3,\n"
            + "        5,\n"
            + "        5\n"
            + "      ]\n"
            + "    },\n"
            + "    \"c\": {\n"
            + "      \"name\": \"just a c!\",\n"
            + "      \"d\": {\n"
            + "        \"values\": [\n"
            + "          1,\n"
            + "          2,\n"
            + "          3,\n"
            + "          4,\n"
            + "          5\n"
            + "        ]\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";

    instantiator.register(A.class, A::new);
    instantiator.register(B.class, B::new);
    instantiator.register(C.class, C::new);
    instantiator.register(D.class, D::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertNotNull(result.b);
    assertEquals(result.b.hello, "world");
    assertArrayEquals(new int[] {1, 2, 3, 4, 5}, result.b.c.d.values);
  }
}
