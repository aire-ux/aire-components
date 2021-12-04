package com.aire.ux.condensation;

import java.io.IOException;
import java.io.OutputStream;
import lombok.NonNull;

public interface DocumentWriter {

  /**
   * @param type the type to write
   * @param value the value of type {@code type} to write
   * @param outputStream the OutputStream to write to
   * @param <T> the type of the value to write
   */
  <T> void write(@NonNull Class<T> type, @NonNull T value, @NonNull OutputStream outputStream)
      throws IOException;
}
