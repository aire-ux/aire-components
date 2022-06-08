package io.sunshower.zephyr.resources;

import static org.junit.jupiter.api.Assertions.*;

import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import io.sunshower.zephyr.AireUITest;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.Kernel;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AireUITest
class ModuleStreamResourceTest {

  @Test
  @SneakyThrows
  void ensureStreamResourceWorks(@Autowired Kernel kernel, @Autowired Module module) {
    val stream = new ModuleStreamResource(kernel, module.getCoordinate(), "images/zephyr.svg");
    val factory = ((StreamResourceWriter) stream.getWriter());
    val baos = new ByteArrayOutputStream();
    factory.accept(baos, VaadinSession.getCurrent());
    assertTrue(baos.toString(StandardCharsets.UTF_8).indexOf("<path") > 0);
  }
}
