package io.sunshower.zephyr.condensation;

import com.aire.ux.condensation.Condensation;
import elemental.json.JsonValue;
import lombok.NonNull;

public class CondensationUtilities {

  static final Condensation condensation;

  static {
    condensation = Condensation.create("json");
  }

  public static final <T> T wrap(@NonNull Class<T> type, @NonNull JsonValue value) {
    return condensation.read(type, value.toJson());
  }

}
