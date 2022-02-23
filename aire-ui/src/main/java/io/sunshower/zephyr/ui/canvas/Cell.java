package io.sunshower.zephyr.ui.canvas;

import io.sunshower.persistence.id.Identifier;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;

public interface Cell {

  Type getType();

  Identifier getId();

  CellTemplate getCellTemplate();

  void setCellTemplate(CellTemplate template);

  void addProperty(@NonNull String key, @NonNull String value);

  Optional<String> clearProperty(@NonNull String key);

  Map<String, String> getProperties();

  enum Type {
    Edge,
    None,
    Vertex;
  }
}
