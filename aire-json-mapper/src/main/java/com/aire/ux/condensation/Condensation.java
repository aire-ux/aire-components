package com.aire.ux.condensation;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import lombok.val;

public class Condensation {

  public static Document parse(String format, CharSequence data) {
    val pf =
        ServiceLoader.load(ParserFactory.class).stream()
            .map(t -> t.get())
            .filter(parserFactory -> parserFactory.supports(format))
            .findAny()
            .orElseThrow(
                () ->
                    new NoSuchElementException(String.format("Unsupported format: '%s'", format)));
    return pf.newParser().parse(data);
  }

  public static <T> T read(
      Class<T> type, String format, CharSequence data, PropertyMappingStrategy strategy) {
    return parse(format, data).read(type, strategy);
  }
}
