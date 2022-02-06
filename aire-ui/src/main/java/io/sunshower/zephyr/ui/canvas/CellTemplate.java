package io.sunshower.zephyr.ui.canvas;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public interface CellTemplate {

  @NonNull
  String getKey();

  @NonNull
  List<Selector> getSelectors();

  @NonNull
  Map<String, Map<String, Serializable>> getAttributes();

  void setAttributes(Map<String, Map<String, Serializable>> attributes);
}
