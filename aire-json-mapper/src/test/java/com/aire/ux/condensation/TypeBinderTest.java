package com.aire.ux.condensation;

import com.aire.ux.condensation.mappings.AnnotationDrivenPropertyScanner;
import com.aire.ux.condensation.mappings.CachingDelegatingTypeInstantiator;
import com.aire.ux.condensation.mappings.ReflectiveTypeInstantiator;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypeBinderTest {

  private PropertyScanner scanner;
  private ReflectiveTypeInstantiator instantiator;

  @BeforeEach
  void setUp() {
    instantiator = new ReflectiveTypeInstantiator();
    scanner =
        new AnnotationDrivenPropertyScanner(new CachingDelegatingTypeInstantiator(instantiator));
    strategy = new DefaultPropertyMappingStrategy(scanner);
  }

  @Test
  void ensureReadingSimpleDocumentWorks() {
    @RootElement
    class A {

      private String name;
    }

    val document = "{\n" + "  \"name\": \"hello\"\n" + "}";
    instantiator.register(A.class, A::new);
    Condensation.read(A.class, "json", document, strategy);
  }
}
