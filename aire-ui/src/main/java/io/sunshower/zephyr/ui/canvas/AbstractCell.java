package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Alias;
import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@RootElement
@EqualsAndHashCode
public abstract class AbstractCell implements Cell {

  @NonNull @Attribute private final Type type;

  @NonNull
  @Attribute(alias = @Alias(read = "id", write = "id"))
  private final Identifier identifier;

  /** a context-dependent identifier for this node */
  @Getter @Setter @Attribute private String key;

  @NonNull @Element private Map<String, String> properties;

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
  public abstract CellTemplate getCellTemplate();

  @Override
  public abstract void setCellTemplate(CellTemplate template);

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
