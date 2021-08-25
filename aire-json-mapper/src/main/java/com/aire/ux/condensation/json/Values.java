package com.aire.ux.condensation.json;

import com.aire.ux.parsing.core.Token;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.val;

public class Values {

  public static Value string(Token value) {
    val lexeme = value.getLexeme();
    return new StringValue(lexeme.substring(1, lexeme.length() - 1));
  }

  public static ObjectValue object() {
    return new ObjectValue();
  }

  public static class StringValue extends AbstractValue<String> {
    public StringValue(String value) {
      super(Type.String, value);
    }
  }

  public static class ObjectValue extends AbstractValue<Map<String, Value>> {
    public ObjectValue() {
      super(Type.Object, new LinkedHashMap<>());
    }

    public void set(String name, Value value) {
      getValue().put(name, value);
    }

    public Value get(String name) {
      return getValue().get(name);
    }
  }
}
