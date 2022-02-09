package io.sunshower.zephyr.ui.canvas.listeners;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import lombok.Getter;
import lombok.NonNull;

@RootElement
public final class ListenerDefinition {

  @Getter
  @Attribute
  private String key;

  @Getter

  @Attribute
  private String category;

  @Getter
  @Attribute
  private Identifier id;

  @Getter
  @Attribute
  private String targetEventType;

  public ListenerDefinition() {

  }

  public ListenerDefinition(@NonNull Identifier id, @NonNull String key, @NonNull String category,
      @NonNull String targetEventType) {
    this.id = id;
    this.key = key;
    this.category = category;
    this.targetEventType = targetEventType;
  }

}
