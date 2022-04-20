package io.sunshower.zephyr.condensation;

import elemental.json.JsonValue;
import io.sunshower.zephyr.ZephyrApplication;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.val;
import org.springframework.core.io.DefaultResourceLoader;

public class CondensationUtilities {

  public static <T> T wrap(@NonNull Class<T> type, @NonNull JsonValue value) {
    return ZephyrApplication.getCondensation().read(type, value.toJson());
  }

  public static <T> T read(Class<T> type, String location) {
    val resourceLoader = new DefaultResourceLoader(Thread.currentThread().getContextClassLoader());
    val resource = resourceLoader.getResource(location);
    try (val inputStream = resource.getInputStream()) {
      return ZephyrApplication.getCondensation()
          .read(type, new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
    } catch (IOException ex) {
      throw new ResourceException(ex);
    }
  }
}
