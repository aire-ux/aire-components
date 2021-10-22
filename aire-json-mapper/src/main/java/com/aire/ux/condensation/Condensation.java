package com.aire.ux.condensation;

import com.aire.ux.condensation.mappings.DefaultTypeBinder;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import lombok.val;

public class Condensation {

  private final String format;
  private final TypeBinder typeBinder;
  private final PropertyScanner scanner;
  private final TypeInstantiator instantiator;


  private Condensation(final String format) {
    this.format = format;
    val condensationConfiguration =
        ServiceLoader.load(CondensationConfiguration.class, Thread.currentThread()
            .getContextClassLoader())
            .stream()
            .map(Provider::get)
            .filter(configuration -> configuration.supports(format))
            .findAny().orElseGet(DefaultCondensationConfiguration::new);
    this.scanner = condensationConfiguration.getScanner();
    this.instantiator = condensationConfiguration.getInstantiator();
    if (condensationConfiguration.providesBinder()) {
      this.typeBinder = condensationConfiguration.createBinder();
    } else {
      this.typeBinder = new DefaultTypeBinder(scanner);
    }
  }


  public static Document parse(String format, CharSequence data) {
    val pf =
        ServiceLoader.load(ParserFactory.class, Thread.currentThread().getContextClassLoader())
            .stream()
            .map(Provider::get)
            .filter(parserFactory -> parserFactory.supports(format))
            .findAny()
            .orElseThrow(
                () ->
                    new NoSuchElementException(String.format("Unsupported format: '%s'", format)));
    return pf.newParser().parse(data);
  }

  public static <T> T read(Class<T> type, String format, CharSequence data, TypeBinder strategy) {
    return parse(format, data).read(type, strategy);
  }

  public static Condensation create(String format) {
    return new Condensation(format);
  }

  public TypeInstantiator getInstantiator() {
    return instantiator;
  }

  public <T> T read(Class<T> type, CharSequence sequence) {
    return read(type, format, sequence, typeBinder);
  }
}
