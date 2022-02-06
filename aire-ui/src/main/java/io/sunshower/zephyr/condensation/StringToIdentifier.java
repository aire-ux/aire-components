package io.sunshower.zephyr.condensation;

import com.aire.ux.condensation.ConverterProvider;
import io.sunshower.lang.tuple.Pair;
import io.sunshower.persistence.id.Identifier;
import java.util.function.Function;

public class StringToIdentifier implements ConverterProvider<String, Identifier> {

  @Override
  public Function<String, Identifier> getConverter() {
    return Identifier::valueOf;
  }

  @Override
  public Pair<Class<String>, Class<Identifier>> getTypeMapping() {
    return Pair.of(String.class, Identifier.class);
  }
}
