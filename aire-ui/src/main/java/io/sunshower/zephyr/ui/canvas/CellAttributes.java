package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RootElement
public class CellAttributes {

  @Attribute
  private Identifier id;

  @Element
  private Map<String, Serializable> attributes;


  public <T extends  Serializable> T addAttribute(String attributeName, T value) {
    if(attributes == null) {
      attributes = new HashMap<>();
    }
    attributes.put(attributeName, value);
    return value;
  }
}
