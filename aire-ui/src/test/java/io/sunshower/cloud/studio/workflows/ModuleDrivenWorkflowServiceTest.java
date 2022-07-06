package io.sunshower.cloud.studio.workflows;

import static org.junit.jupiter.api.Assertions.*;

import io.sunshower.zephyr.AireUITest;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.Kernel;
import java.util.stream.StreamSupport;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AireUITest
class ModuleDrivenWorkflowServiceTest {

  @Test
  void ensureWorkflowServiceIsRegistered(@Autowired Kernel kernel, @Autowired Module module) {
    val registrations = kernel.getServiceRegistry().getRegistrations(module);
    assertTrue(
        StreamSupport.stream(registrations.spliterator(), false)
            .anyMatch(registration -> registration.provides(WorkflowService.class)));
  }
}
