package io.sunshower.zephyr.condensation;

import io.sunshower.arcus.condensation.ConverterProvider;
import io.sunshower.lang.tuple.Pair;
import io.sunshower.persistence.id.Identifier;
import java.util.function.Function;

public class IdentifierToString implements ConverterProvider<Identifier, String> {

  @Override
  public Function<Identifier, String> getConverter() {
    return Identifier::toString;
  }

  @Override
  public Pair<Class<Identifier>, Class<String>> getTypeMapping() {
    return Pair.of(Identifier.class, String.class);
  }
}
