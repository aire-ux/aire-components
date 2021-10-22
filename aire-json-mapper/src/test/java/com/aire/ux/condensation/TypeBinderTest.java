package com.aire.ux.condensation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.condensation.mappings.AnnotationDrivenPropertyScanner;
import com.aire.ux.condensation.mappings.CachingDelegatingTypeInstantiator;
import com.aire.ux.condensation.mappings.DefaultTypeBinder;
import com.aire.ux.condensation.mappings.ReflectiveTypeInstantiator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.SneakyThrows;
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
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_Doubles_field() {
    @RootElement
    class A {

      @Attribute
      private Double[] doubles;
    }

    val document = "\n" + "{\n" + "  \"doubles\": [1,2,3,4,5, 1e-17,1e7, -1E-17]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new Double[]{1d, 2d, 3d, 4d, 5d, 1e-17, 1e7, -1E-17}, result.doubles);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_doubles_field() {
    @RootElement
    class A {

      @Attribute
      private double[] doubles;
    }

    val document = "\n" + "{\n" + "  \"doubles\": [1,2,3,4,5, 1e-17,1e7, -1E-17]\n" + "}";
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

    val document = "\n" + "{\n" + "  \"floats\": [1,2,3,4,5, 1e-3,1e4, -1E-7]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(
        new float[]{1, 2, 3, 4, 5, (float) 1e-3, (float) 1e4, (float) -1E-7}, result.floats);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_Float_field() {
    @RootElement
    class A {

      @Attribute
      private Float[] floats;
    }

    val document = "\n" + "{\n" + "  \"floats\": [1,2,3,4,5, 1e-3,1e4, -1E-7]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new Float[]{1f, 2f, 3f, 4f, 5f, 1e-3f, 1e4f, -1E-7f}, result.floats);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_Boolean_field() {
    @RootElement
    class A {

      @Attribute
      private Boolean[] booleans;
    }

    val document = "\n" + "{\n" + "  \"booleans\": [true, false, true,   true]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new Boolean[]{true, false, true, true}, result.booleans);
  }

  @Test
  void ensureReadingDoubleArrayIntoDoubleArrayWorks_boolean_field() {
    @RootElement
    class A {

      @Attribute
      private boolean[] booleans;
    }

    val document = "\n" + "{\n" + "  \"booleans\": [true, false, true,   true]\n" + "}";
    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new boolean[]{true, false, true, true}, result.booleans);
  }

  @Test
  void ensureReadingStringArrayWorks() {
    val document = "{\n" + "  \"strings\": [\"one\", \"two\", \"three!\"]\n" + "}";

    @RootElement
    class A {

      @Attribute
      private String[] strings;
    }

    instantiator.register(A.class, A::new);

    val result = Condensation.read(A.class, "json", document, binder);
    assertArrayEquals(new String[]{"one", "two", "three!"}, result.strings);
  }

  @Test
  void ensureReadingNestedObjectWorks() {
    @RootElement
    class B {

      @Attribute
      String hello;
    }
    @RootElement
    class A {

      @Element
      private B b;
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

      @Element
      int[] values;
    }
    @RootElement
    class C {

      @Element
      D d;

      @Element
      String name;
    }
    @RootElement
    class B {

      @Attribute
      String hello;

      @Element
      private C c;
      @Element
      private D d;
    }
    @RootElement
    class A {

      @Attribute
      String name;

      @Element
      private B b;
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
    assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.b.c.d.values);
  }

  @Test
  void ensureMappingSimpleCollectionOfIntsWorks() {

    val document = "\n"
                   + "{\n"
                   + "  \"my-list\": [1,2,3,4,5]\n"
                   + "}";

    @RootElement
    class ListTest {

      @Element(alias = @Alias(read = "my-list"))
      private List<Integer> elements;
    }

    instantiator.register(ListTest.class, ListTest::new);
    val result = Condensation.read(ListTest.class, "json", document, binder);
    assertEquals(List.of(1, 2, 3, 4, 5), result.elements);
  }

  @Test
  void ensureReadingArrayOfPrimitivesIntsWorks() {
    val doc = "[\n"
              + "  1,\n"
              + "  2,\n"
              + "  3\n"
              + "]";

    val read = Condensation.read(int[].class, "json", doc, binder);
    assertArrayEquals(read, new int[]{1, 2, 3});
  }

  @Test
  void ensureReadingArrayOfPrimitivesFloatsWorks() {
    val doc = "[\n"
              + "  1,\n"
              + "  2,\n"
              + "  3\n"
              + "]";

    val read = Condensation.read(float[].class, "json", doc, binder);
    assertArrayEquals(read, new float[]{1, 2, 3});
  }


  @Test
  void ensureReadingArrayOfPrimitivesLongsWorks() {
    val doc = "[\n"
              + "  1,\n"
              + "  2,\n"
              + "  3\n"
              + "]";

    val read = Condensation.read(long[].class, "json", doc, binder);
    assertArrayEquals(read, new long[]{1, 2, 3});
  }

  @Test
  void ensureReadingArrayOfPrimitivesStringsWorks() {
    val doc = "[\n"
              + "  \"1\",\n"
              + "  \"2\",\n"
              + "  \"3\"\n"
              + "]";

    val read = Condensation.read(String[].class, "json", doc, binder);
    assertArrayEquals(read, new String[]{"1", "2", "3"});
  }

  @Test
  void ensureReadingListOfComplexElementsWorks() throws NoSuchFieldException {

    @RootElement
    class Content {

      @Attribute
      String name;
    }
    @RootElement
    class Container {

      @Element
      private List<Content> contents;
    }

    instantiator.register(Content.class, Content::new);
    instantiator.register(Container.class, Container::new);

    val value = "{\n"
                + "  \"contents\": [\n"
                + "    {\n"
                + "      \"name\": \"Josiah\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"name\": \"Frances\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";

    val result = Condensation.read(Container.class, "json", value, binder);
    assertEquals(2, result.contents.size());
    assertInstanceOf(Content.class, result.contents.get(0));
  }

  @Test
  void ensureReadingPolymorphicValuesWorks() {
    @RootElement
    @Discriminator(field = "@type")
    class Content {

      @Attribute
      String name;
      @Attribute(
          alias = @Alias(read = "@type")
      )
      @Convert(TypeConverter.class)
      private Class<? extends Content> type;
    }
    @RootElement
    class Container {

      @Element
      private List<Content> contents;
    }
    @RootElement
    class SubContent extends Content {

      @Attribute
      private String value;
    }

    instantiator.register(Content.class, Content::new);
    instantiator.register(SubContent.class, SubContent::new);
    instantiator.register(Container.class, Container::new);

    val value = String.format("{\n"
                              + "  \"contents\": [\n"
                              + "    {\n"
                              + "      \"name\": \"Josiah\",\n"
                              + "      \"@type\": \"%s\"\n"
                              + "    },\n"
                              + "    {\n"
                              + "      \"name\": \"Frances\",\n"
                              + "      \"@type\": \"%s\",\n"
                              + "      \"value\": \"Whatever\"\n"
                              + "    }\n"
                              + "  ]\n"
                              + "}", Content.class.getName(), SubContent.class.getName());

    val result = Condensation.read(Container.class, "json", value, binder);
    assertEquals(2, result.contents.size());
    assertInstanceOf(Content.class, result.contents.get(0));
  }


  @Test
  void ensureReadingPolymorphicValuesWorksForArray() {
    @RootElement
    @Discriminator(field = "@type")
    class Content {

      @Attribute
      String name;
      @Attribute(
          alias = @Alias(read = "@type")
      )
      @Convert(TypeConverter.class)
      private Class<? extends Content> type;
    }
    @RootElement
    class Container {

      @Element
      private Content[] contents;
    }
    @RootElement
    class SubContent extends Content {

      @Attribute
      private String value;
    }

    instantiator.register(Content.class, Content::new);
    instantiator.register(SubContent.class, SubContent::new);
    instantiator.register(Container.class, Container::new);

    val value = String.format("{\n"
                              + "  \"contents\": [\n"
                              + "    {\n"
                              + "      \"name\": \"Josiah\",\n"
                              + "      \"@type\": \"%s\"\n"
                              + "    },\n"
                              + "    {\n"
                              + "      \"name\": \"Frances\",\n"
                              + "      \"@type\": \"%s\",\n"
                              + "      \"value\": \"Whatever\"\n"
                              + "    }\n"
                              + "  ]\n"
                              + "}", Content.class.getName(), SubContent.class.getName());

    val result = Condensation.read(Container.class, "json", value, binder);
    assertEquals(2, result.contents.length);
    assertInstanceOf(Content.class, result.contents[0]);
    assertInstanceOf(SubContent.class, result.contents[1]);
  }

  @Test
  void ensureSimpleMapWorks() {

    @RootElement
    class MapTest {

      @Element
      Map<String, Integer> values;
    }

    val doc = "{\n"
              + "  \"values\": {\n"
              + "    \"1\": 1,\n"
              + "    \"2\": 2 \n"
              + "  }\n"
              + "}";
    instantiator.register(MapTest.class, MapTest::new);

    val result = Condensation.read(MapTest.class, "json", doc, binder);
    assertEquals(result.values.get("1"), 1);
  }


  @Test
  void ensureMapContainingValuesWorks() {

    @RootElement
    class Value {
      @Attribute
      String name;
    }
    @RootElement
    class MapTest {

      @Element
      Map<String, Value> values;
    }
    val doc = "{\n"
              + "  \"values\": {\n"
              + "    \"josiah\": {\n"
              + "      \"name\" : \"joebees\"\n"
              + "    },\n"
              + "\n"
              + "    \"lisa\": {\n"
              + "      \"name\" : \"wabbus\"\n"
              + "    },\n"
              + "\n"
              + "    \"fran\": {\n"
              + "      \"name\" : \"the custard\"\n"
              + "    }\n"
              + "  }\n"
              + "}";

    instantiator.register(MapTest.class, MapTest::new);
    instantiator.register(Value.class, Value::new);

    val result = Condensation.read(MapTest.class, "json", doc, binder);
    assertEquals(result.values.get("josiah").name, "joebees");
  }

  public static class TypeConverter<T> implements Function<String, Class<T>> {

    public TypeConverter() {
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Class<T> apply(String s) {
      return (Class<T>) Class.forName(s, true, Thread.currentThread().getContextClassLoader());
    }
  }


}
