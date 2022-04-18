package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.ui.canvas.Cell;
import lombok.Getter;
import lombok.NonNull;

@RootElement
public final class ListenerDefinition {

  @Getter @Attribute private String key;
  @Getter @Attribute private Cell.Type type;

  @Getter @Attribute private String category;

  @Getter @Attribute private Identifier id;

  @Getter @Attribute private String targetEventType;

  public ListenerDefinition() {}

  public ListenerDefinition(
      @NonNull Identifier id,
      @NonNull String key,
      @NonNull Cell.Type type,
      @NonNull String category,
      @NonNull String targetEventType) {
    this.id = id;
    this.key = key;
    this.type = type;
    this.category = category;
    this.targetEventType = targetEventType;
  }
}
