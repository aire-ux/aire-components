package com.aire.ux.condensation;

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
    System.out.println(document);
    instantiator.register(A.class, A::new);
    val result = Condensation.read(A.class, "json", document, binder);
    assertEquals(Double.valueOf(value).intValue(), result.name);
  }
}
