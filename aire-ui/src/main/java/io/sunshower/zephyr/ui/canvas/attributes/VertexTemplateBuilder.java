package io.sunshower.zephyr.ui.canvas.attributes;

import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.val;

public class VertexTemplateBuilder {

  private final VertexTemplate template;

  public VertexTemplateBuilder() {
    template = new VertexTemplate();
  }

  public VertexTemplateBuilderWithWidthOrHeight width(Number f) {
    return new VertexTemplateBuilderWithWidthOrHeight().width(f);
  }

  public VertexTemplateBuilderWithWidthOrHeight height(Number f) {
    return new VertexTemplateBuilderWithWidthOrHeight().height(f);
  }

  public VertexTemplate create() {
    return template;
  }

  public VertexTemplateAttributeBuilder attribute(String image) {
    return new VertexTemplateAttributeBuilder().attribute(image);
  }

  public VertexTemplateBuilder name(String name) {
    template.setName(name);
    return this;
  }


  public class VertexTemplateBuilderWithWidthOrHeight {

    public VertexTemplateBuilderWithWidthOrHeight width(Number f) {
      template.setWidth(f.floatValue());
      return this;
    }

    public VertexTemplateBuilderWithWidthOrHeight height(Number f) {
      template.setHeight(f.floatValue());
      return this;
    }

    public VertexTemplateAttributeBuilder attribute(String attribute) {
      return new VertexTemplateAttributeBuilder().attribute(attribute);
    }
  }

  public class VertexTemplateAttributeBuilder {

    final Map<String, Map<String, Serializable>> attributes;
    private Map<String, Serializable> values;

    public VertexTemplateAttributeBuilder() {
      val attrs = template.getAttributes();
      if (attrs == null) {
        attributes = new HashMap<>();
        template.setAttributes(attributes);
      } else {
        attributes = attrs;
      }
    }

    public VertexTemplateAttributeBuilder attribute(String attribute) {
      values = attributes.compute(attribute, (k, v) -> v == null ? new HashMap<>() : v);
      return this;
    }

    public VertexTemplatePropertyBuilder property(String key) {
      return new VertexTemplatePropertyBuilder(key, values, this);
    }

    public MarkupBuilder tagName(String tagname) {
      return new MarkupBuilder(tagname);
    }

    public VertexTemplate create() {
      return template;
    }
  }


  public class VertexTemplatePropertyBuilder {

    private final String key;
    private final Map<String, Serializable> values;
    private final VertexTemplateAttributeBuilder parent;

    public VertexTemplatePropertyBuilder(@NonNull String key,
        @NonNull Map<String, Serializable> values,
        @NonNull VertexTemplateAttributeBuilder parent) {
      this.key = key;
      this.values = values;
      this.parent = parent;
    }

    public VertexTemplateAttributeBuilder hex(String s) {
      values.put(key, "#" + s);
      return parent;
    }

    public VertexTemplateAttributeBuilder string(String s) {
      values.put(key, s);
      return parent;
    }

    public VertexTemplateAttributeBuilder number(Number s) {
      values.put(key, s.floatValue());
      return parent;
    }
  }

  public class MarkupBuilder {

    final String tagName;


    private MarkupBuilder(String tagName) {
      this.tagName = tagName;
    }

    public VertexTemplateBuilder selector(String body) {
      template.addSelector(tagName, body);
      return VertexTemplateBuilder.this;
    }
  }
}
