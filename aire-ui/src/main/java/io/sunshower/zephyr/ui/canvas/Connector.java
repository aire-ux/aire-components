package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Alias;
import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@RootElement
public class Connector {

  @Getter @Setter @Attribute private String name;

  @Getter
  @Setter
  @Element(alias = @Alias(read = "args", write = "args"))
  private Map<String, Serializable> arguments;

  public Connector() {
    arguments = new HashMap<>();
  }

  public void addArgument(String key, Serializable value) {
    arguments.put(key, value);
  }
}
