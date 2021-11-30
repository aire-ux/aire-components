package com.aire.ux.control.canvas;

import com.aire.ux.condensation.Condensation;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import com.vaadin.flow.internal.JsonCodec;
import java.io.IOException;
import lombok.val;
import org.junit.jupiter.api.Test;

public class JsonTest {

  @Test
  void ensureJsonHandlesStuffLikeIExpect() throws IOException {
    @RootElement
    class A {
      @Element
      String name = "coolbeans";
    }
    val result = Condensation.write("json", A.class, new A());
    val r = JsonCodec.encodeWithTypeInfo(result);
  }

}
