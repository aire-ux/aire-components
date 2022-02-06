package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Alias;
import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@RootElement
@EqualsAndHashCode
public abstract class AbstractCell implements Cell {

  @NonNull @Attribute private final Type type;

  @NonNull
  @Attribute(alias = @Alias(read = "id", write = "id"))
  private final Identifier identifier;

  @NonNull @Element private Map<String, String> properties;

  @Element private CellTemplate template;

  protected AbstractCell(final Type type, final @NonNull Identifier identifier) {
    this.type = type;
    this.identifier = identifier;
    this.properties = new HashMap<>();
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public Identifier getId() {
    return identifier;
  }

  @Override
  public CellTemplate getCellTemplate() {
    return template;
  }

  @Override
  public void setCellTemplate(CellTemplate template) {
    this.template = template;
  }

  @Override
  public void addProperty(@NonNull String key, @NonNull String value) {
    properties.put(key, value);
  }

  @Override
  public Optional<String> clearProperty(@NonNull String key) {
    return Optional.ofNullable(properties.remove(key));
  }

  @Override
  public Map<String, String> getProperties() {
    return Collections.unmodifiableMap(properties);
  }
}
