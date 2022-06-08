package io.sunshower.zephyr.resources;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import io.zephyr.kernel.Coordinate;
import io.zephyr.kernel.core.Kernel;
import java.io.InputStream;
import lombok.val;

@SuppressWarnings("PMD.UseProperClassLoader")
public class ModuleStreamResource extends StreamResource {

  static final String PREFIX = "ZEPHYR-INF/resources/";

  public ModuleStreamResource(Kernel kernel, Coordinate host, String name) {
    super(suffix(name), new ModuleStreamResourceFactory(host, normalize(name), kernel));
  }

  static String normalize(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Resource location must not be null or empty");
    }
    if (name.charAt(0) == '/') {
      name = name.substring(1);
    }
    if (name.startsWith(PREFIX)) {
      return name;
    } else {
      return PREFIX + name;
    }
  }

  static String suffix(String name) {
    val idx = name.lastIndexOf('/');
    if (idx == -1) {
      return name;
    }
    return name.substring(idx + 1);
  }

  static final class ModuleStreamResourceFactory implements InputStreamFactory {

    private final String name;
    private final Kernel kernel;
    private final Coordinate host;

    public ModuleStreamResourceFactory(Coordinate host, String name, Kernel kernel) {
      this.host = host;
      this.name = name;
      this.kernel = kernel;
    }

    @Override
    public InputStream createInputStream() {
      return kernel.getModuleManager().getModule(host).getClassLoader().getResourceAsStream(name);
    }
  }
}
