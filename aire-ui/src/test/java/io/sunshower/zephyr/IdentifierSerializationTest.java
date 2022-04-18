package io.sunshower.zephyr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sunshower.arcus.condensation.Alias;
import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.arcus.condensation.Convert;
import io.sunshower.arcus.condensation.Converter;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.lang.common.encodings.Base58;
import io.sunshower.lang.common.encodings.Base58.Alphabets;
import io.sunshower.lang.common.encodings.Encoding;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IdentifierSerializationTest {

  Condensation condensation;

  @BeforeEach
  void setUp() {
    condensation = Condensation.create("json");
  }

  @Test
  @SneakyThrows
  void ensureIdentifiersAreSerializedCorrectly() {
    val element = new TestElement();
    val id = Identifiers.newSequence().next();
    element.setIdentifier(id);
    val s = condensation.getWriter().write(TestElement.class, element);
    val el = condensation.read(TestElement.class, s);
    assertEquals(id, el.getIdentifier());
    System.out.println(el);
    System.out.println(s);
  }

  @Test
  @SneakyThrows
  void ensureNestedElementsAreReadCorrectly() {
    val element = new TestElement();
    val id = Identifiers.newSequence().next();
    element.setIdentifier(id);
    val holder = new TestElementHolder();
    holder.elements.add(element);
    val s = condensation.getWriter().write(TestElementHolder.class, holder);
    System.out.println(s);
    val elements = condensation.read(TestElementHolder.class, s);
    assertEquals(id, elements.elements.get(0).identifier);
  }

  @Test
  @SneakyThrows
  void ensureParentElementsWork() {
    val element = new ChildElement();
    val id = Identifiers.newSequence().next();
    element.setIdentifier(id);
    val holder = new TestElementHolder();
    holder.elements.add(element);
    val s = condensation.getWriter().write(TestElementHolder.class, holder);
    System.out.println(s);
    val elements = condensation.read(TestElementHolder.class, s);
    assertEquals(id, elements.elements.get(0).identifier);
  }

  @RootElement
  public static class TestElementHolder {

    @Element private List<TestElement> elements;

    public TestElementHolder() {
      this.elements = new ArrayList<>();
    }
  }

  @RootElement
  public static class ChildElement extends TestElement {}

  @RootElement
  public static class TestElement {

    @Getter
    @Setter
    @Attribute(alias = @Alias(read = "@id", write = "@id"))
    @Convert(Base58Decoder.class)
    private Identifier identifier;

    public TestElement() {}
  }

  public static class Base58Decoder implements Converter<Identifier, String> {

    static final Encoding encoding = Base58.getInstance(Alphabets.Default);

    public Identifier apply(String s) {
      return Identifier.valueOf(encoding.decode(s));
    }

    @Override
    public Identifier read(String s) {
      return apply(s);
    }

    @Override
    public String write(Identifier identifier) {
      return encoding.encode(identifier.getId());
    }
  }
}
