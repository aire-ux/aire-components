package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Alias;
import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@RootElement
public class AbstractCellTemplate implements CellTemplate {

  @Getter @Setter @Attribute private String key;

  @Element(alias = @Alias(read = "markup", write = "markup"))
  private List<Selector> selectors;

  @Element(alias = @Alias(read = "attrs", write = "attrs"))
  private Map<String, Map<String, Serializable>> attributes;

  protected AbstractCellTemplate() {
    this.selectors = new ArrayList<>();
    this.attributes = new LinkedHashMap<>();
  }

  protected AbstractCellTemplate(@NonNull String key) {
    this();
    setKey(key);
  }

  @Override
  public List<Selector> getSelectors() {
    return Collections.unmodifiableList(selectors);
  }

  @Override
  public Map<String, Map<String, Serializable>> getAttributes() {
    return attributes;
  }

  @Override
  public void setAttributes(Map<String, Map<String, Serializable>> attributes) {
    this.attributes = attributes;
  }

  public void addSelector(String tagName, String selector) {
    selectors.add(new Selector(tagName, selector));
  }
}
