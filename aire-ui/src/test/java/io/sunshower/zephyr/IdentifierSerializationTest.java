package io.sunshower.zephyr;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Condensation;
import com.aire.ux.condensation.Convert;
import com.aire.ux.condensation.RootElement;
import io.sunshower.lang.common.encodings.Base58;
import io.sunshower.lang.common.encodings.Base58.Alphabets;
import io.sunshower.lang.common.encodings.Encoding;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import java.util.function.Function;
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
    element.setIdentifier(Identifiers.newSequence().next());
    val s = condensation.getWriter().write(TestElement.class, element);
    System.out.println(s);

  }

  @RootElement
  public static class TestElement {

    public TestElement() {

    }

    @Getter
    @Setter
    @Attribute
    @Convert(Base58Decoder.class)
    private Identifier identifier;

  }


  public static class Base58Decoder implements
      Function<String, Identifier> {

    static final Encoding encoding = Base58.getInstance(Alphabets.Default);

    @Override
    public Identifier apply(String s) {
      return Identifier.valueOf(encoding.decode(s));
    }
  }


}
