package com.aire.ux.control.designer.servlet;

import com.aire.ux.condensation.Condensation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.val;

public class DefaultDesignerConfigurationProvider implements DesignerConfigurationProvider {


  @Override
  public DesignerConfiguration load() {
    try (val reader = new BufferedReader(
        new InputStreamReader(Objects
            .requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("ZEPHYR-INF/aire-designer.configuration.json"))))) {
      val result = reader.lines().collect(Collectors.joining());
      return Condensation.create("json").read(DesignerConfiguration.class, result);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }


}
